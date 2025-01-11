package ru.nsu.concert_mate.user_service.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RefreshFirebaseTokenBodyModel {
    @JsonProperty(value = "old_token")
    private String oldToken;
    @JsonProperty(value = "new_token")
    private String newToken;
}
