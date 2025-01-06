package ru.nsu.concert_mate.user_service.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthDto {
    @JsonProperty(value = "user_id")
    private long userId;

    private String code;

    private String email;
}
