package ru.nsu.concert_mate.user_service.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RefreshTokenBodyModel {
    @JsonProperty(value = "refresh_token")
    public String refreshToken;
    @JsonProperty(value = "firebase_token")
    private String firebaseToken;
}
