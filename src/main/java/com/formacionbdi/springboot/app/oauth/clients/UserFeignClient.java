package com.formacionbdi.springboot.app.oauth.clients;

import com.formacionbdi.springboot.app.commons.usuarios.models.entity.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(value = "servicio-usuarios")
public interface UserFeignClient {

    @GetMapping("/users/search/find-by-username")
    public User findByUsername(@RequestParam String username);

    @PutMapping("/users/{id}")
    public User update(@RequestBody User user, @PathVariable Long id);
}
