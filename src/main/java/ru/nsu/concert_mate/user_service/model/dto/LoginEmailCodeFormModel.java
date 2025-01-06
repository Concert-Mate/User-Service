package ru.nsu.concert_mate.user_service.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class LoginEmailCodeFormModel {
    private String email;
    private String code;
    private String firebaseToken;
}
