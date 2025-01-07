package ru.nsu.concert_mate.user_service.model.dto;

import lombok.Data;

@Data
public class RefreshTokenBodyModel {
    public String refreshToken;
    private String firebaseToken;
}
