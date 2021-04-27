package com.formacionbdi.springboot.app.oauth.event;

import brave.Tracer;
import com.formacionbdi.springboot.app.commons.usuarios.models.entity.User;
import com.formacionbdi.springboot.app.oauth.services.IUserService;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;


@Component
public class AuthenticationSuccessErrorHandler implements AuthenticationEventPublisher {

    private Logger log = LoggerFactory.getLogger(AuthenticationSuccessErrorHandler.class);

    @Autowired
    private IUserService iUserService;

    @Autowired
    private Tracer tracer;

    @Override
    public void publishAuthenticationSuccess(Authentication authentication) {
        if(authentication.getName().equalsIgnoreCase("frontendapp")){
            return; // si es igual a frontendapp se salen del método!
        }

        UserDetails user = (UserDetails) authentication.getPrincipal();
        String message = "Success Login: " + user.getUsername();
        System.out.println(message);
        log.info(message);

        User usuario = iUserService.findByUsername(user.getUsername());

        if(usuario.getIntentos() != null && usuario.getIntentos() > 0){
            usuario.setIntentos(0);
            iUserService.update(usuario, usuario.getId());
        }

    }

    @Override
    public void publishAuthenticationFailure(AuthenticationException e, Authentication authentication) {
        String message = "Error en el login: " + e.getMessage();
        log.error(message + " : ");
        System.out.println(message);
        StringBuilder errors = new StringBuilder();
        try {
            errors.append(message);
            User user = iUserService.findByUsername(authentication.getName());

            if (user.getIntentos() == null) {
                user.setIntentos(0);
            }
            String errorAttemp = String.format("Intento actual, %s de ingreso del usuario.", user.getIntentos());
            log.error(errorAttemp);
            errors.append(" - " +errorAttemp);
            String errorMaxAttemps = String.format("Maximo de intento superado usuario %s se encuenta bloqueado.", user.getUsername());
            user.setIntentos(user.getIntentos() + 1);
            if (user.getIntentos() >= 3){

                errors.append(errorMaxAttemps);
                user.setEnabled(false);
            }

            iUserService.update(user, user.getId());

            tracer.currentSpan().tag("error.mensaje", errors.toString());
        } catch (FeignException exception) {
            log.error(String.format("El usuario %s no existe en el sistema", authentication.getName()));
        }
    }
}
