package com.mc.scl.auth.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mc.scl.auth.enums.Status;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Table(name = "service_auth_token")
@Entity
@Data
@NoArgsConstructor
public class ServiceAuthToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;

    private String serviceCode;

    private String serviceAuthKey;

    private String allowedIps;

    @Enumerated(EnumType.STRING)
    private Status status;

    private Date createdAt;

    private Date updatedAt;

    private Date expiresAt;
}
