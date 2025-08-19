package com.mc.scl.security.service;

import jakarta.servlet.http.HttpServletRequest;

public interface UserAuthenticationService {
    boolean validateSession(HttpServletRequest request);
}
