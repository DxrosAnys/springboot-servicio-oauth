package com.formacionbdi.springboot.app.oauth.services;

import com.formacionbdi.springboot.app.commons.usuarios.models.entity.User;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface IUserService {

    User findByUsername(String username);

    User update( User user, Long id);
}
