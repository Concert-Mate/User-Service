package ru.nsu.concert_mate.user_service.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class LogoutBodyModel {
    @JsonProperty(value = "refresh_token")
    private String refreshToken;
    @JsonProperty(value = "firebase_token")
    private String firebaseToken;
}
