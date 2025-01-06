package ru.nsu.concert_mate.user_service.repositories;

import org.springframework.data.repository.CrudRepository;
import ru.nsu.concert_mate.user_service.model.entities.AccessTokenEmbeddedEntity;
import ru.nsu.concert_mate.user_service.model.entities.AccessTokenEntity;
import ru.nsu.concert_mate.user_service.model.entities.AuthEmbeddedEntity;
import ru.nsu.concert_mate.user_service.model.entities.AuthEntity;

import java.util.Optional;

public interface AccessTokenBlackListRepository extends CrudRepository<AccessTokenEntity, AccessTokenEmbeddedEntity> {
}
