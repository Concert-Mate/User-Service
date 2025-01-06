package ru.nsu.concert_mate.user_service.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class LogoutBodyModel {
    private String refreshToken;
    private String firebaseToken;
}
