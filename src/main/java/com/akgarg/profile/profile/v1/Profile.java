package com.akgarg.profile.profile.v1;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
public class Profile {

    private String id;
    private String username;
    private String email;
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
    private String forgotPasswordToken;
    private boolean premiumAccount;
    private boolean deleted;
    private Long lastPasswordChangedAt;
    private Long lastLoginAt;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public Profile(final String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Profile{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", password='" + password +
                '}';
    }

}
