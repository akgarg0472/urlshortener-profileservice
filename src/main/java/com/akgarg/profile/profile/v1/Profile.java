package com.akgarg.profile.profile.v1;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.sql.Timestamp;

@Getter
@Setter
@ToString
public class Profile {

    private String id;
    private String username;
    private String email;

    @ToString.Exclude
    private String password;

    private String scopes;
    private String name;
    private String bio;
    private String phone;
    private String city;
    private String state;
    private String country;
    private String zipcode;
    private String businessDetails;

    @ToString.Exclude
    private String forgotPasswordToken;

    private Long lastPasswordChangedAt;
    private Long lastLoginAt;
    private boolean premiumAccount;
    private Timestamp createdAt;
    private Timestamp updatedAt;

}
