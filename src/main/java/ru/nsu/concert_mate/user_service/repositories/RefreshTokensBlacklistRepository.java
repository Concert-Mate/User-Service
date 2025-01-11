package ru.nsu.concert_mate.user_service.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import ru.nsu.concert_mate.user_service.model.entities.RefreshTokenEntity;

import java.util.Optional;

public interface RefreshTokensBlacklistRepository extends CrudRepository<RefreshTokenEntity, String> {
    @Query(value = "SELECT * FROM public.refresh_tokens WHERE token = :token",
            nativeQuery = true)
    Optional<RefreshTokenEntity> findByToken(String token);
}
