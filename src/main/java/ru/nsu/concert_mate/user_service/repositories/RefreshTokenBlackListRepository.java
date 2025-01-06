package ru.nsu.concert_mate.user_service.repositories;

import org.springframework.data.repository.CrudRepository;
import ru.nsu.concert_mate.user_service.model.entities.*;

import java.util.Optional;

public interface RefreshTokenBlackListRepository extends CrudRepository<RefreshTokenEntity, RefreshTokenEmbeddedEntity> {
}
