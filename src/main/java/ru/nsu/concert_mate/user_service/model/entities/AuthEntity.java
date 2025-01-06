package ru.nsu.concert_mate.user_service.model.entities;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "users_codes", uniqueConstraints = {
        @UniqueConstraint(name = "users_codes_pk", columnNames = {"user_id", "created_at"})
})
@NoArgsConstructor
@EqualsAndHashCode
public class AuthEntity {
    @EmbeddedId
    @Column(unique = true)
    private AuthEmbeddedEntity auth;

    public AuthEntity(long userId, String code, String email) {
        auth = new AuthEmbeddedEntity(userId, code, email, new Date());
    }
}
