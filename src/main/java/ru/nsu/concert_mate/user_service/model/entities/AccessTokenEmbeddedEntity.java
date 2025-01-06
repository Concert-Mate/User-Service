package ru.nsu.concert_mate.user_service.model.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Table(name = "users_access_tokens", uniqueConstraints = {
        @UniqueConstraint(name = "users_access_tokens_pk", columnNames = {"user_id", "token"})
})
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class AccessTokenEmbeddedEntity implements Serializable {
    @Column(name = "user_id", nullable = false)
    private long userId;

    @Column(name = "token", nullable = false)
    private String token;
}
