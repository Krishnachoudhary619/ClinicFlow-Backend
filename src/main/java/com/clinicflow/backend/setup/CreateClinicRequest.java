package com.clinicflow.backend.setup;

import lombok.Data;

@Data
public class CreateClinicRequest {

    private String clinicName;
    private String address;
    private String city;
    private String phoneNumber;

    private String adminName;
    private String adminEmail;
    private String adminPhone;
    private String password;
}
