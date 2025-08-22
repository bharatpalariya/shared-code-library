package com.mc.scl.security.service;

import com.mc.scl.auth.dao.ServiceAuthTokenDao;
import com.mc.scl.auth.entity.ServiceAuthToken;
import com.mc.scl.exception.CommonException;
import com.mc.scl.exception.CommonExceptionMessages;
import com.mc.scl.logger.AppLogger;
import com.mc.scl.logger.model.LogLayout;
import com.mc.scl.auth.enums.Status;
import com.mc.scl.util.VariablesConstant;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Service responsible for service authentication operations including
 * authentication key validation and IP address validation.
 */
@Service
@RequiredArgsConstructor
public class ClientAuthenticationService {

    private final AppLogger appLogger;

    private final ServiceAuthTokenDao serviceAuthTokenDao;

    /**
     * Validates service requests using Auth Key and IP validation
     */
    public boolean validateServiceRequest(HttpServletRequest request) throws CommonException {
        String serviceCode = request.getHeader("X-Service-Code");
        String serviceAuthKey = request.getHeader("X-Service-Auth-Key");
        if (Objects.isNull(serviceCode) || serviceCode.trim().isEmpty()) {
            throw new CommonException(CommonExceptionMessages.INVALID_AUTHENTICATION);
        }
        if (Objects.isNull(serviceAuthKey) || serviceAuthKey.trim().isEmpty()) {
            throw new CommonException(CommonExceptionMessages.INVALID_AUTHENTICATION);
        }
        serviceCode = serviceCode.trim();
        serviceAuthKey = serviceAuthKey.trim();
        ServiceAuthToken serviceAuthToken = serviceAuthTokenDao.findByServiceCodeAndServiceAuthKeyAndStatus(serviceCode, serviceAuthKey,
                Status.ACTIVE);
        if (Objects.isNull(serviceAuthToken)) {
            throw new CommonException(CommonExceptionMessages.UNAUTHORIZED_SERVICE);
        }
        serviceIpCheck(serviceAuthToken, request);
        return true;
    }

    /**
     * Validates the service IP address against database whitelist
     * Checks X-Forwarded-For, X-Real-IP, and Remote Address
     */
    public void serviceIpCheck(ServiceAuthToken serviceAuthTokens, HttpServletRequest httpServletRequest) throws CommonException {
        if (Objects.nonNull(serviceAuthTokens.getAllowedIps())) {
            List<String> allowedIps = Arrays.asList(serviceAuthTokens.getAllowedIps().split(","));
            String remoteIpAddress = httpServletRequest.getRemoteAddr();
            String initiatedIpAddress = httpServletRequest.getHeader("X-FORWARDED-FOR");
            if (!(allowedIps.contains(remoteIpAddress) || allowedIps.contains(initiatedIpAddress))) {
                LogLayout logLayout = new LogLayout(
                        VariablesConstant.WARNING,
                        VariablesConstant.LOG_NAME_AUTHENTICATION,
                        VariablesConstant.MODULE_SECURITY,
                        this.getClass().getName(),
                        Thread.currentThread().getStackTrace()[1].getMethodName(),
                        "SystemAllowedIps:" + allowedIps,
                        "remoteIpAddress:" + remoteIpAddress,
                        "X-FORWARDED-FOR:" + initiatedIpAddress,
                        VariablesConstant.EMPTY_STRING
                );
                appLogger.writeLog(logLayout, null);
                throw new CommonException(CommonExceptionMessages.IP_ADDRESS_MISSING);
            }
        }
    }
}

