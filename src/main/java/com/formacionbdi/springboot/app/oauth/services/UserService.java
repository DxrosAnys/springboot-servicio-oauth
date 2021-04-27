package com.formacionbdi.springboot.app.oauth.services;

import brave.Tracer;
import com.formacionbdi.springboot.app.commons.usuarios.models.entity.User;
import com.formacionbdi.springboot.app.oauth.clients.UserFeignClient;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService implements IUserService, UserDetailsService {

    private final Logger log = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserFeignClient client;

    @Autowired
    private Tracer tracer;

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        try {
            User user = client.findByUsername(userName);

            List<GrantedAuthority> grantedAuthorities = user.getRoles().stream()
                    .peek(authority -> log.info("Role: " + authority.getName()))
                    .map(role -> new SimpleGrantedAuthority(role.getName()))
                    .collect(Collectors.toList());
            return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),
                    user.getEnabled(), true, true, true, grantedAuthorities);
        } catch (FeignException e) {
            String error = "Error en el login, no existe el usuario'" + userName + "' en el sistema";

            tracer.currentSpan().tag("error.mensaje", error + ":" + e.getMessage());
            throw new UsernameNotFoundException(error);
        }
    }

    @Override
    public User findByUsername(String username) {
        return client.findByUsername(username);
    }

    @Override
    public User update(User user, Long id) {
        return client.update(user, id);
    }
}
