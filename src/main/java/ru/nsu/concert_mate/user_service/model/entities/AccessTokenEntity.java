package ru.nsu.concert_mate.user_service.model.entities;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users_access_tokens", uniqueConstraints = {
        @UniqueConstraint(name = "users_access_tokens_pk", columnNames = {"user_id", "token"})
})
@NoArgsConstructor
@EqualsAndHashCode
public class AccessTokenEntity {
    @EmbeddedId
    @Column(unique = true)
    private AccessTokenEmbeddedEntity accessToken;

    public AccessTokenEntity(long userId, String token) {
        accessToken = new AccessTokenEmbeddedEntity(userId, token);
    }
}
