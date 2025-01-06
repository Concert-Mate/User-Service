package ru.nsu.concert_mate.user_service.services.users;

import ru.nsu.concert_mate.user_service.model.dto.AuthDto;
import ru.nsu.concert_mate.user_service.model.dto.UserDto;
import ru.nsu.concert_mate.user_service.model.entities.AuthEntity;
import ru.nsu.concert_mate.user_service.services.users.exceptions.UserNotFoundException;

import java.util.List;
import java.util.Optional;

public interface AuthService {
    void addAuthCode(long userId, String code, String email);

    AuthDto findByEmail(String email);
}
