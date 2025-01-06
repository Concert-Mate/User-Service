package ru.nsu.concert_mate.user_service.repositories;

import org.springframework.data.repository.CrudRepository;
import ru.nsu.concert_mate.user_service.model.entities.AuthEmbeddedEntity;
import ru.nsu.concert_mate.user_service.model.entities.AuthEntity;
import ru.nsu.concert_mate.user_service.model.entities.FirebaseTokenEmbeddedEntity;
import ru.nsu.concert_mate.user_service.model.entities.FirebaseTokenEntity;

import java.util.Optional;

public interface FirebaseTokenRepository extends CrudRepository<FirebaseTokenEntity, FirebaseTokenEmbeddedEntity> {
}
