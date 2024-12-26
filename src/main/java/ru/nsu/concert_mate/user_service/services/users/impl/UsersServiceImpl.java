package ru.nsu.concert_mate.user_service.services.users.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import ru.nsu.concert_mate.user_service.model.dto.UserDto;
import ru.nsu.concert_mate.user_service.model.entities.UserEntity;
import ru.nsu.concert_mate.user_service.repositories.UsersRepository;
import ru.nsu.concert_mate.user_service.services.users.UsersService;
import ru.nsu.concert_mate.user_service.services.users.exceptions.UserNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UsersServiceImpl implements UsersService {
    private final UsersRepository usersRepository;
    private final ModelMapper modelMapper;

    @Override
    public UserDto addUser(long telegramId) {
        UserEntity userEntity = new UserEntity(telegramId);
        usersRepository.save(userEntity);
        log.info("successfully added user {}", telegramId);
        return modelMapper.map(userEntity, UserDto.class);
    }

    @Override
    public UserDto deleteUser(long telegramId) throws UserNotFoundException {
        final Optional<UserEntity> optionalUser = usersRepository.findByTelegramId(telegramId);

        if (optionalUser.isPresent()) {
            final UserEntity userEntity = optionalUser.get();
            usersRepository.delete(userEntity);
            log.info("successfully deleted user {}", telegramId);
            return modelMapper.map(userEntity, UserDto.class);
        } else {
            log.warn("can't delete user {} because there is no user with this telegram id", telegramId);
            throw new UserNotFoundException();
        }
    }

    @Override
    public Optional<UserDto> findUser(long telegramId) {
        final Optional<UserEntity> optionalUserEntity = usersRepository.findByTelegramId(telegramId);
        if (optionalUserEntity.isPresent()) {
            log.info("successfully found user with telegram id {}", telegramId);
            return Optional.ofNullable(modelMapper.map(optionalUserEntity, UserDto.class));
        } else {
            log.error("can't find user with telegram id {}", telegramId);
            return Optional.empty();
        }
    }

    @Override
    public List<UserDto> findAllUsers() {
        final List<UserDto> result = new ArrayList<>();

        for (final var it : usersRepository.findAll()) {
            result.add(modelMapper.map(it, UserDto.class));
        }
        log.info("successfully found all users");
        return result;
    }
}
