package ru.nsu.concert_mate.user_service.services.users.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import ru.nsu.concert_mate.user_service.model.dto.UserDto;
import ru.nsu.concert_mate.user_service.model.entities.FirebaseTokenEntity;
import ru.nsu.concert_mate.user_service.model.entities.UserEntity;
import ru.nsu.concert_mate.user_service.repositories.FirebaseTokenRepository;
import ru.nsu.concert_mate.user_service.repositories.UsersRepository;
import ru.nsu.concert_mate.user_service.services.users.FirebaseService;
import ru.nsu.concert_mate.user_service.services.users.UsersService;
import ru.nsu.concert_mate.user_service.services.users.exceptions.TokenNotFoundException;
import ru.nsu.concert_mate.user_service.services.users.exceptions.UserNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class FirebaseServiceImpl implements FirebaseService {
    private final FirebaseTokenRepository firebaseTokenRepository;
    private final ModelMapper modelMapper;


    @Override
    public void addToken(String token, long userId) {
        firebaseTokenRepository.save(new FirebaseTokenEntity(userId, token));
    }

    @Override
    public void updateToken(String oldToken, String newToken) throws TokenNotFoundException {
        Optional<FirebaseTokenEntity> optionalToken = firebaseTokenRepository.findByToken(oldToken);

        if (optionalToken.isPresent()) {
            final FirebaseTokenEntity tokenEntity = optionalToken.get();
            firebaseTokenRepository.save(new FirebaseTokenEntity(tokenEntity.getUserId(), newToken));
            firebaseTokenRepository.delete(tokenEntity);
            log.info("successfully updated token {} to {}", oldToken, newToken);
        } else {
            log.warn("Token {} not found", oldToken);
            throw new TokenNotFoundException();
        }
    }

    @Override
    public void deleteToken(String token) throws TokenNotFoundException {
        Optional<FirebaseTokenEntity> optionalToken = firebaseTokenRepository.findByToken(token);

        if (optionalToken.isPresent()) {
            final FirebaseTokenEntity tokenEntity = optionalToken.get();
            firebaseTokenRepository.delete(tokenEntity);
            log.info("successfully deleted token {}", token);
        } else {
            log.warn("Token {} not found", token);
            throw new TokenNotFoundException();
        }
    }

    @Override
    public FirebaseTokenEntity getToken(long userId) throws UserNotFoundException {
        Optional<FirebaseTokenEntity> optionalToken =  firebaseTokenRepository.findByUserId(userId);
        if (optionalToken.isPresent()) {
            return optionalToken.get();
        } else {
            log.warn("User with id {} not found", userId);
            throw new UserNotFoundException();
        }
    }
}
