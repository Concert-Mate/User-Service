package ru.nsu.concert_mate.user_service.model.dto;

import lombok.Data;

@Data
public class RefreshFirebaseTokenBodyModel {
    private String oldToken;
    private String newToken;
}
