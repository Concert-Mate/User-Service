package ru.nsu.concert_mate.user_service.services.users.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.nsu.concert_mate.user_service.model.entities.RefreshTokenEntity;
import ru.nsu.concert_mate.user_service.repositories.RefreshTokensBlacklistRepository;
import ru.nsu.concert_mate.user_service.repositories.UsersRepository;
import ru.nsu.concert_mate.user_service.services.users.RefreshTokensService;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshTokensServiceImpl implements RefreshTokensService {

    private final RefreshTokensBlacklistRepository refreshTokensBlacklistRepository;

    @Override
    public void blacklistToken(String token) {
        refreshTokensBlacklistRepository.save(new RefreshTokenEntity(token));
    }

    @Override
    public boolean isTokenBlacklisted(String token) {
        return refreshTokensBlacklistRepository.findByToken(token).isPresent();
    }
}
