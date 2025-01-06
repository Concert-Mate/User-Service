package ru.nsu.concert_mate.user_service.api.users;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RefreshToken {
    private String id;
    private long exp;
}
