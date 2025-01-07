package ru.nsu.concert_mate.user_service.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import ru.nsu.concert_mate.user_service.model.entities.FirebaseTokenEmbeddedEntity;
import ru.nsu.concert_mate.user_service.model.entities.FirebaseTokenEntity;
import ru.nsu.concert_mate.user_service.model.entities.RefreshTokenEntity;

import java.util.Optional;

public interface FirebaseTokenRepository extends CrudRepository<FirebaseTokenEntity, FirebaseTokenEmbeddedEntity> {
    @Query(value = "SELECT * FROM public.users_firebase_tokens WHERE token = :token",
            nativeQuery = true)
    Optional<FirebaseTokenEntity> findByToken(long token);
}
