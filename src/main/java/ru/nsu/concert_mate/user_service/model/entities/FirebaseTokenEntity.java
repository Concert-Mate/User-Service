package ru.nsu.concert_mate.user_service.model.entities;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users_firebase_tokens", uniqueConstraints = {
        @UniqueConstraint(name = "users_firebase_tokens_pk", columnNames = {"user_id", "token"})
})
@NoArgsConstructor
@EqualsAndHashCode
public class FirebaseTokenEntity {
    @EmbeddedId
    @Column(unique = true)
    private FirebaseTokenEmbeddedEntity firebaseToken;

    public FirebaseTokenEntity(long userId, String token) {
        firebaseToken = new FirebaseTokenEmbeddedEntity(userId, token);
    }
    public long getUserId(){
        return firebaseToken.getUserId();
    }
    public String getToken(){
        return firebaseToken.getToken();
    }
}
