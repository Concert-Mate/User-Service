package ru.nsu.concert_mate.user_service.model.entities;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "refresh_tokens")
@NoArgsConstructor
@EqualsAndHashCode
public class RefreshTokenEntity {
    @Id
    @Column(name = "token", nullable = false)
    private String token;

    @Column(name = "created_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDatetime;

    public RefreshTokenEntity(String token) {
        this.token = token;
        this.creationDatetime = new Date();
    }
}
