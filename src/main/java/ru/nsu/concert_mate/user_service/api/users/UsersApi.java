package ru.nsu.concert_mate.user_service.api.users;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.nsu.concert_mate.user_service.model.dto.*;

@RequestMapping(value = "/users")
public interface UsersApi {
    @PostMapping("/login")
    ResponseEntity<DetailResponse> emailLogin(LoginEmailFormModel loginEmailFormModel);

    @PutMapping("/login")
    ResponseEntity<TokensResponse> loginWithEmailCode(@Valid @RequestBody LoginEmailCodeFormModel loginEmailCodeFormModel);

    @PostMapping("/logout")
    ResponseEntity<DetailResponse> logout(@RequestHeader("Authorization") String token,
                          @Valid @RequestBody LogoutBodyModel logoutBodyModel);

    @PostMapping("/refresh")
    ResponseEntity<DetailResponse> refresh(@Valid @RequestBody RefreshTokenBodyModel refreshTokenBodyModel);

    @GetMapping("/cities")
    ResponseEntity<UserCitiesResponse> getUserCities(@RequestHeader("Authorization") String token);

    @PostMapping("/cities")
    ResponseEntity<DetailResponse> addUserCity(
            @RequestHeader("Authorization") String token,
            @RequestParam(name = "city", required = false) String cityName
    );

    @DeleteMapping("/cities")
    ResponseEntity<DetailResponse> deleteUserCity(
            @RequestHeader("Authorization") String token,
            @RequestParam(name = "city") String cityName
    );

    @GetMapping("/track-lists")
    ResponseEntity<UserTrackListsResponse> getUserTrackLists(@RequestHeader("Authorization") String token);

    @PostMapping("/track-lists")
    ResponseEntity<UserTrackListResponse> addUserTrackList(
            @RequestHeader("Authorization") String token,
            @RequestParam(name = "url") String trackListUrl
    );

    @DeleteMapping("/track-lists")
    ResponseEntity<DetailResponse> deleteUserTrackList(
            @RequestHeader("Authorization") String token,
            @RequestParam(name = "url") String trackListUrl
    );

    @GetMapping("/concerts")
    ResponseEntity<UserConcertsResponse> getUserConcerts(@RequestHeader("Authorization") String token);

    @PutMapping("/firebase-token")
    ResponseEntity<DetailResponse> putFirebaseToken(@RequestHeader("Authorization") String token,
                                    @Valid @RequestBody RefreshFirebaseTokenBodyModel refreshFirebaseTokenBodyModel);
}
