package ru.nsu.concert_mate.user_service.services.users;

import ru.nsu.concert_mate.user_service.model.dto.UserDto;
import ru.nsu.concert_mate.user_service.services.users.exceptions.UserNotFoundException;

import java.util.List;
import java.util.Optional;

public interface UsersService {
    UserDto addUser(String email, String code);

    UserDto deleteUser(long telegramId) throws UserNotFoundException;

    Optional<UserDto> findUser(long id);

    List<UserDto> findAllUsers();

    UserDto findByEmail(String email);
}
