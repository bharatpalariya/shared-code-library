package com.mc.scl.auth.dao_impl;

import com.mc.scl.auth.dao.ServiceAuthTokenDao;
import com.mc.scl.auth.entity.ServiceAuthToken;
import com.mc.scl.auth.enums.Status;
import com.mc.scl.auth.repo.ServiceAuthTokenRepo;

public class ServiceAuthTokenDaoImpl implements ServiceAuthTokenDao {
    
    private final ServiceAuthTokenRepo repo;
    
    public ServiceAuthTokenDaoImpl(ServiceAuthTokenRepo repo) {
        this.repo = repo;
    }

    @Override
    public ServiceAuthToken findByServiceCodeAndServiceAuthKeyAndStatus(String serviceCode, String serviceAuthKey, Status status) {
        ServiceAuthToken result = repo.findByServiceCodeAndServiceAuthKeyAndStatus(serviceCode, serviceAuthKey, status);
        isEmptyOrNull(result == null);
        return result;
    }


}
