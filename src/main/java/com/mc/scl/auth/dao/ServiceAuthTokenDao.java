package com.mc.scl.auth.dao;

import com.mc.scl.auth.entity.ServiceAuthToken;
import com.mc.scl.auth.enums.Status;
import com.mc.scl.exception.CommonException;
import com.mc.scl.exception.CommonExceptionMessages;

public interface ServiceAuthTokenDao {
    ServiceAuthToken findByServiceCodeAndServiceAuthKeyAndStatus(String serviceCode, String serviceAuthKey, Status status);

    default void isEmptyOrNull(Boolean bool) {
        if (Boolean.TRUE.equals(bool))
            throw new CommonException(CommonExceptionMessages.DATA_NOT_FOUND);
    }
}
