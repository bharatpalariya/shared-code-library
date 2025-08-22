package com.mc.scl.auth.repo;

import com.mc.scl.auth.entity.ServiceAuthToken;
import com.mc.scl.auth.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceAuthTokenRepo extends JpaRepository<ServiceAuthToken, Long> {

    ServiceAuthToken findByServiceCodeAndServiceAuthKeyAndStatus(String serviceCode, String serviceAuthKey, Status status);

}

