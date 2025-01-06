package ru.nsu.concert_mate.user_service.model.entities;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users_refresh_tokens", uniqueConstraints = {
        @UniqueConstraint(name = "users_refresh_tokens_pk", columnNames = {"user_id", "token"})
})
@NoArgsConstructor
@EqualsAndHashCode
public class RefreshTokenEntity {
    @EmbeddedId
    @Column(unique = true)
    private RefreshTokenEmbeddedEntity refreshToken;

    public RefreshTokenEntity(long userId, String token) {
        refreshToken = new RefreshTokenEmbeddedEntity(userId, token);
    }
}
