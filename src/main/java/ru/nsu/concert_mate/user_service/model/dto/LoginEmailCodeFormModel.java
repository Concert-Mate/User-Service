package ru.nsu.concert_mate.user_service.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class LoginEmailCodeFormModel {
    private String email;
    private String code;
    @JsonProperty(value = "firebase_token")
    private String firebaseToken;
}
