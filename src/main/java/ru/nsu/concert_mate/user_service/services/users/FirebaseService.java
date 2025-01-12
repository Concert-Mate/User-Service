package ru.nsu.concert_mate.user_service.services.users;

import ru.nsu.concert_mate.user_service.model.dto.UserDto;
import ru.nsu.concert_mate.user_service.model.entities.FirebaseTokenEntity;
import ru.nsu.concert_mate.user_service.services.users.exceptions.TokenNotFoundException;
import ru.nsu.concert_mate.user_service.services.users.exceptions.UserNotFoundException;

import java.util.List;
import java.util.Optional;

public interface FirebaseService {
    void addToken(String token, long userId);

    void updateToken(String oldToken, String newToken) throws TokenNotFoundException;

    void deleteToken(String token) throws TokenNotFoundException;

    FirebaseTokenEntity getToken(long userId) throws UserNotFoundException;
}
