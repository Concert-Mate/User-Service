package ru.nsu.concert_mate.user_service.model.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Embeddable
@Table(name = "users_codes", uniqueConstraints = {
        @UniqueConstraint(name = "users_codes_pk", columnNames = {"user_id", "created_at"})
})
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class AuthEmbeddedEntity implements Serializable {
    @Column(name = "user_id", nullable = false)
    private long userId;

    @Column(name = "auth_code", nullable = false)
    private String authCode;

    @Column(name = "auth_email", nullable = false)
    private String authEmail;

    @Column(name = "created_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDatetime;
}
