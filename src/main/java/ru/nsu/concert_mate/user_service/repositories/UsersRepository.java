package ru.nsu.concert_mate.user_service.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import ru.nsu.concert_mate.user_service.model.entities.UserEntity;

import java.util.Optional;

public interface UsersRepository extends CrudRepository<UserEntity, Long> {
    @Query(value = "SELECT * FROM public.users WHERE id = :id",
            nativeQuery = true)
    Optional<UserEntity> findById(long id);
    @Query(value = "SELECT * FROM public.users WHERE email = :email",
            nativeQuery = true)
    Optional<UserEntity> findByEmail(String email);

}
