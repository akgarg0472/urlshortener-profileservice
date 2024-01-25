package com.akgarg.profile.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class UpdateProfileRequest {

    @JsonIgnore
    private MultipartFile profilePicture;

    @JsonProperty("name")
    private String name;

    @JsonProperty("bio")
    private String bio;

    @JsonProperty("phone")
    private String phone;

    @JsonProperty("city")
    private String city;

    @JsonProperty("state")
    private String state;

    @JsonProperty("country")
    private String country;

    @JsonProperty("zipcode")
    private String zipcode;

    @JsonProperty("business_details")
    private String businessDetails;

    @Override
    public String toString() {
        return "UpdateProfileRequest{" +
                "profilePicture=" + (profilePicture != null) +
                ", name='" + name + '\'' +
                ", bio='" + bio + '\'' +
                ", phone='" + phone + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", country='" + country + '\'' +
                ", zipcode='" + zipcode + '\'' +
                ", businessDetails='" + businessDetails + '\'' +
                '}';
    }

}
