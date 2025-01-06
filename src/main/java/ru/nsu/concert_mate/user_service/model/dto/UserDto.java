package ru.nsu.concert_mate.user_service.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@Data
@EqualsAndHashCode
public class UserDto {
    private long id;

    private String code;

    private String email;

    @JsonProperty(value = "creation_datetime")
    private Date creationDatetime;
}
