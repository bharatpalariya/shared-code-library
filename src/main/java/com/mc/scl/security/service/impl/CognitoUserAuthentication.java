package com.mc.scl.security.service.impl;

import com.mc.scl.security.service.UserAuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

@Service
public class CognitoUserAuthentication implements UserAuthenticationService {

    @Override
    public boolean validateSession(HttpServletRequest request) {
        return true;
    }
}
