package ru.nsu.concert_mate.user_service.api.users;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.nsu.concert_mate.user_service.api.exception.IncorrectAuthCodeException;
import ru.nsu.concert_mate.user_service.api.exception.ParseTokenException;
import ru.nsu.concert_mate.user_service.api.exception.TokenBlacklistedException;
import ru.nsu.concert_mate.user_service.api.exception.TokenExpiredException;
import ru.nsu.concert_mate.user_service.model.dto.*;
import ru.nsu.concert_mate.user_service.services.cities.CitiesServiceException;
import ru.nsu.concert_mate.user_service.services.cities.CityNotFoundException;
import ru.nsu.concert_mate.user_service.services.music.exceptions.MusicServiceException;
import ru.nsu.concert_mate.user_service.services.users.exceptions.*;

@RequestMapping(value = "/users")
public interface UsersApi {
    @PostMapping("/login")
    ResponseEntity<DetailResponse> emailLogin(@Valid @RequestBody LoginEmailFormModel loginEmailFormModel);

    @PutMapping("/login")
    ResponseEntity<TokensResponse> loginWithEmailCode(@Valid @RequestBody LoginEmailCodeFormModel loginEmailCodeFormModel) throws IncorrectAuthCodeException, UserNotFoundException;

    @PostMapping("/logout")
    ResponseEntity<DetailResponse> logout(@RequestHeader("Authorization") String accessToken,
                                          @Valid @RequestBody LogoutBodyModel logoutBodyModel) throws ParseTokenException, TokenExpiredException, TokenBlacklistedException, TokenNotFoundException;

    @PostMapping("/refresh")
    ResponseEntity<TokensResponse> refresh(@Valid @RequestBody RefreshTokenBodyModel refreshTokenBodyModel) throws TokenExpiredException, ParseTokenException, TokenBlacklistedException;

    @GetMapping("/cities")
    ResponseEntity<UserCitiesResponse> getUserCities(@RequestHeader("Authorization") String accessToken) throws TokenExpiredException, ParseTokenException, TokenBlacklistedException, UserNotFoundException, InternalErrorException;

    @PostMapping("/cities")
    ResponseEntity<DetailResponse> addUserCity(
            @RequestHeader("Authorization") String accessToken,
            @RequestParam(name = "city", required = false) String cityName
    ) throws TokenExpiredException, ParseTokenException, TokenBlacklistedException, CitiesServiceException, InternalErrorException, CityNotFoundException, UserNotFoundException, CityAlreadyAddedException;

    @DeleteMapping("/cities")
    ResponseEntity<DetailResponse> deleteUserCity(
            @RequestHeader("Authorization") String accessToken,
            @RequestParam(name = "city") String cityName
    ) throws TokenExpiredException, ParseTokenException, TokenBlacklistedException, UserNotFoundException, CityNotAddedException;

    @GetMapping("/track-lists")
    ResponseEntity<UserTrackListsResponse> getUserTrackLists(@RequestHeader("Authorization") String accessToken) throws TokenExpiredException, ParseTokenException, TokenBlacklistedException, UserNotFoundException, InternalErrorException, TrackListNotAddedException;

    @PostMapping("/track-lists")
    ResponseEntity<UserTrackListResponse> addUserTrackList(
            @RequestHeader("Authorization") String accessToken,
            @RequestParam(name = "url") String trackListUrl
    ) throws TokenExpiredException, ParseTokenException, TokenBlacklistedException, MusicServiceException, InternalErrorException, UserNotFoundException, TrackListAlreadyAddedException;

    @DeleteMapping("/track-lists")
    ResponseEntity<DetailResponse> deleteUserTrackList(
            @RequestHeader("Authorization") String accessToken,
            @RequestParam(name = "url") String trackListUrl
    ) throws ParseTokenException, UserNotFoundException, TrackListNotAddedException, MusicServiceException, InternalErrorException, TokenExpiredException, TokenBlacklistedException;

    @GetMapping("/concerts")
    ResponseEntity<UserConcertsResponse> getUserConcerts(@RequestHeader("Authorization") String accessToken) throws TokenExpiredException, ParseTokenException, TokenBlacklistedException, UserNotFoundException, InternalErrorException, TrackListNotAddedException;

    @PutMapping("/firebase-token")
    ResponseEntity<DetailResponse> putFirebaseToken(@RequestHeader("Authorization") String accessToken,
                                                    @Valid @RequestBody RefreshFirebaseTokenBodyModel refreshFirebaseTokenBodyModel) throws TokenExpiredException, ParseTokenException, TokenBlacklistedException, TokenNotFoundException;
}
