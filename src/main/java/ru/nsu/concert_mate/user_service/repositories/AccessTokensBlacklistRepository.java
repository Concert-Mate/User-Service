package ru.nsu.concert_mate.user_service.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import ru.nsu.concert_mate.user_service.model.entities.AccessTokenEntity;

import java.util.Optional;

public interface AccessTokensBlacklistRepository extends CrudRepository<AccessTokenEntity, String> {
    @Query(value = "SELECT * FROM public.access_tokens WHERE token = :token",
            nativeQuery = true)
    Optional<AccessTokenEntity> findByToken(String token);
}
