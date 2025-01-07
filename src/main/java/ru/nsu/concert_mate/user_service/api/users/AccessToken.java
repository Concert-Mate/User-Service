package ru.nsu.concert_mate.user_service.api.users;

import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.nsu.concert_mate.user_service.api.exception.ParseTokenException;

import java.util.Base64;

@Getter
@AllArgsConstructor
public class AccessToken {

    private String sub;

    private long exp;

    static public AccessToken parseFromString(String token) throws ParseTokenException {
        try {
            String[] chunks = token.split("\\.");
            Base64.Decoder decoder = Base64.getUrlDecoder();
            String payload = new String(decoder.decode(chunks[1]));
            return new Gson().fromJson(payload, AccessToken.class);
        } catch (Exception e) {
            throw new ParseTokenException(token);
        }
    }

}
