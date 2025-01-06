package ru.nsu.concert_mate.user_service.services.users.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import ru.nsu.concert_mate.user_service.model.dto.AuthDto;
import ru.nsu.concert_mate.user_service.model.dto.UserDto;
import ru.nsu.concert_mate.user_service.model.entities.AuthEntity;
import ru.nsu.concert_mate.user_service.model.entities.UserEntity;
import ru.nsu.concert_mate.user_service.repositories.AuthRepository;
import ru.nsu.concert_mate.user_service.repositories.UsersRepository;
import ru.nsu.concert_mate.user_service.services.users.AuthService;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final AuthRepository authRepository;
    private final ModelMapper modelMapper;

    @Override
    public void addAuthCode(long userId, String code, String email) {
        AuthEntity authEntity = new AuthEntity(userId, code, email);
        authRepository.save(authEntity);
        log.info("successfully added authEntity: {} for user with id: {}", authEntity, userId);
    }

    @Override
    public AuthDto findByEmail(String email) {
        AuthEntity authEntity = authRepository.findByEmail(email).get();
        return modelMapper.map(authEntity, AuthDto.class);
    }
}
