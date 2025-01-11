package ru.nsu.concert_mate.user_service.services.users.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.nsu.concert_mate.user_service.model.entities.AccessTokenEntity;
import ru.nsu.concert_mate.user_service.model.entities.RefreshTokenEntity;
import ru.nsu.concert_mate.user_service.repositories.AccessTokensBlacklistRepository;
import ru.nsu.concert_mate.user_service.repositories.RefreshTokensBlacklistRepository;
import ru.nsu.concert_mate.user_service.services.users.AccessTokensService;
import ru.nsu.concert_mate.user_service.services.users.RefreshTokensService;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccessTokensServiceImpl implements AccessTokensService {

    private final AccessTokensBlacklistRepository accessTokensBlacklistRepository;

    @Override
    public void blacklistToken(String token) {
        accessTokensBlacklistRepository.save(new AccessTokenEntity(token));
    }

    @Override
    public boolean isTokenBlacklisted(String token) {
        return accessTokensBlacklistRepository.findByToken(token).isPresent();
    }
}
