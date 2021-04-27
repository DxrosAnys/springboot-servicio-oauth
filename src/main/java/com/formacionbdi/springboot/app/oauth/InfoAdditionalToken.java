package com.formacionbdi.springboot.app.oauth;

import com.formacionbdi.springboot.app.commons.usuarios.models.entity.User;
import com.formacionbdi.springboot.app.oauth.services.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class InfoAdditionalToken implements TokenEnhancer {

    @Autowired
    private IUserService iUserService;

    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken oAuth2AccessToken, OAuth2Authentication oAuth2Authentication) {
        Map<String, Object> info = new HashMap<String, Object>();

        User user = iUserService.findByUsername(oAuth2Authentication.getName());

        info.put("name", user.getUsername());
        info.put("lastname", user.getLastName());
        info.put("mail", user.getEmail());
        ((DefaultOAuth2AccessToken) oAuth2AccessToken).setAdditionalInformation(info);

        return oAuth2AccessToken;
    }
}
