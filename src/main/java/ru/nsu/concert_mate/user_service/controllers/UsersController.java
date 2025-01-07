package ru.nsu.concert_mate.user_service.controllers;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.nsu.concert_mate.user_service.api.exception.IncorrectAuthCodeException;
import ru.nsu.concert_mate.user_service.api.exception.ParseTokenException;
import ru.nsu.concert_mate.user_service.api.exception.TokenBlacklistedException;
import ru.nsu.concert_mate.user_service.api.exception.TokenExpiredException;
import ru.nsu.concert_mate.user_service.api.users.*;
import ru.nsu.concert_mate.user_service.model.dto.*;
import ru.nsu.concert_mate.user_service.services.cities.*;
import ru.nsu.concert_mate.user_service.services.email.EmailService;
import ru.nsu.concert_mate.user_service.services.music.MusicService;
import ru.nsu.concert_mate.user_service.services.music.exceptions.MusicServiceException;
import ru.nsu.concert_mate.user_service.services.users.*;
import ru.nsu.concert_mate.user_service.services.users.exceptions.*;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.*;

@RestController()
@RequiredArgsConstructor
@Log
public class UsersController implements UsersApi {
    private final UsersService usersService;
    private final UsersCitiesService usersCitiesService;
    private final UsersTrackListsService usersTrackListsService;
    private final MusicService musicService;
    private final UsersShownConcertsService shownConcertsService;
    private final CitiesService citiesService;
    private final EmailService emailService;
    private final RefreshTokensService refreshTokensService;
    private final AccessTokensService accessTokensService;
    @Value("${spring.auth.access-token-expiration-time}")
    private final long ACCESS_TOKEN_EXPIRATION_TIME;
    @Value("${spring.auth.refresh-token-expiration-time}")
    private final long REFRESH_TOKEN_EXPIRATION_TIME;
    @Value("${spring.auth.signing-key}")
    private String jwtSigningKey;
    SecretKey secretKey = Keys.hmacShaKeyFor(jwtSigningKey.getBytes(StandardCharsets.UTF_8));

    @Override
    public ResponseEntity<DetailResponse> emailLogin(LoginEmailFormModel loginEmailFormModel){
        String code = generateCode();
        if(usersService.findByEmail(loginEmailFormModel.getEmail()).isPresent()){
            var user = usersService.findByEmail(loginEmailFormModel.getEmail()).get();
            user.setCode(code);
            usersService.updateUser(user);
        }
        else{
            usersService.addUser(loginEmailFormModel.getEmail(), code);
        }
        emailService.sendMail(loginEmailFormModel.getEmail(), "Concert Mate code", "Your authentication code: " + code);
        return ResponseEntity.ok(new DetailResponse("Email code sended"));
    }

    @Override
    public ResponseEntity<TokensResponse> loginWithEmailCode(LoginEmailCodeFormModel loginEmailCodeFormModel) throws IncorrectAuthCodeException, UserNotFoundException {
        Date now = new Date();
        if(usersService.findByEmail(loginEmailCodeFormModel.getEmail()).isEmpty()){
            throw new UserNotFoundException();
        }
        var user = usersService.findByEmail(loginEmailCodeFormModel.getEmail()).get();
        if(!user.getCode().equals(loginEmailCodeFormModel.getCode())){
            throw new IncorrectAuthCodeException(user.getCode());
        }
        String accessToken = Jwts.builder()
                .subject(String.valueOf(user.getId()))
                .expiration(new Date(now.getTime() + ACCESS_TOKEN_EXPIRATION_TIME))
                .signWith(secretKey)
                .compact();
        String refreshToken = Jwts.builder()
                .subject(String.valueOf(user.getId()))
                .expiration(new Date(now.getTime() + REFRESH_TOKEN_EXPIRATION_TIME))
                .signWith(secretKey)
                .compact();
        TokensResponse tokensResponse = new TokensResponse(accessToken,refreshToken);
        return ResponseEntity.ok(tokensResponse);
    }


    @Override
    public ResponseEntity<DetailResponse> logout(String accessToken, LogoutBodyModel logoutBodyModel) throws ParseTokenException, TokenExpiredException, TokenBlacklistedException {
        verifyAccessToken(accessToken);
        accessTokensService.blacklistToken(accessToken);
        refreshTokensService.blacklistToken(logoutBodyModel.getRefreshToken());
        return ResponseEntity.ok(new DetailResponse("sad"));
    }

    @Override
    public ResponseEntity<TokensResponse> refresh(RefreshTokenBodyModel refreshTokenBodyModel) throws TokenExpiredException, ParseTokenException, TokenBlacklistedException {
        verifyRefreshToken(refreshTokenBodyModel.refreshToken);
        refreshTokensService.blacklistToken(refreshTokenBodyModel.refreshToken);
        String userId = RefreshToken.parseFromString(refreshTokenBodyModel.refreshToken).getSub();
        Date now = new Date();
        String accessToken = Jwts.builder()
                .subject(String.valueOf(userId))
                .expiration(new Date(now.getTime() + ACCESS_TOKEN_EXPIRATION_TIME))
                .signWith(secretKey)
                .compact();
        String refreshToken = Jwts.builder()
                .subject(String.valueOf(userId))
                .expiration(new Date(now.getTime() + REFRESH_TOKEN_EXPIRATION_TIME))
                .signWith(secretKey)
                .compact();
        TokensResponse tokensResponse = new TokensResponse(accessToken,refreshToken);
        return ResponseEntity.ok(tokensResponse);
    }

    @Override
    public ResponseEntity<UserCitiesResponse> getUserCities(String accessToken) throws TokenExpiredException, ParseTokenException, TokenBlacklistedException, UserNotFoundException, InternalErrorException {
        verifyAccessToken(accessToken);
        long userId = Long.parseLong(AccessToken.parseFromString(accessToken).getSub());
        return ResponseEntity.ok(new UserCitiesResponse(usersCitiesService.getUserCities(userId)));
    }

    @Override
    public ResponseEntity<DetailResponse> addUserCity(String accessToken, String cityName) throws TokenExpiredException, ParseTokenException, TokenBlacklistedException, CitiesServiceException, InternalErrorException, CityNotFoundException, UserNotFoundException, CityAlreadyAddedException {
        verifyAccessToken(accessToken);
        String cityToAdd;
        var res = citiesService.findCity(cityName);
        if(res.getCode() == CitySearchByNameCode.FUZZY){
            return ResponseEntity.ok(new DetailResponse(res.getOptions().get(0).getName()));
        }
        if (res.getCode() == CitySearchByNameCode.NOT_FOUND) {
            throw new CityNotFoundException();
        }
        if (res.getCode() == CitySearchByNameCode.SUCCESS) {
            cityToAdd = res.getOptions().get(0).getName();
        }
        else {
            throw new InternalErrorException();
        }
        usersCitiesService.saveUserCity(Long.parseLong(AccessToken.parseFromString(accessToken).getSub()), cityToAdd);
        return ResponseEntity.ok(new DetailResponse("City successfully added"));
    }

    @Override
    public ResponseEntity<DetailResponse>  deleteUserCity(String accessToken, String cityName) throws TokenExpiredException, ParseTokenException, TokenBlacklistedException, UserNotFoundException, CityNotAddedException {
        verifyAccessToken(accessToken);
        usersCitiesService.deleteUserCity(Long.parseLong(AccessToken.parseFromString(accessToken).getSub()), cityName);
        return ResponseEntity.ok(new DetailResponse("City successfully deleted"));
    }

    @Override
    public ResponseEntity<UserTrackListsResponse> getUserTrackLists(String accessToken) throws TokenExpiredException, ParseTokenException, TokenBlacklistedException, UserNotFoundException, InternalErrorException, TrackListNotAddedException {
        verifyAccessToken(accessToken);
        final List<String> trackLists = usersTrackListsService.getUserTrackLists(Long.parseLong(AccessToken.parseFromString(accessToken).getSub()));
        final List<TrackListHeaderDto> result = new ArrayList<>();
        for (String trackList : trackLists) {
            try {
                TrackListDto trackListDto = musicService.getTrackListData(trackList);
                result.add(new TrackListHeaderDto(trackListDto.getUrl(), trackListDto.getTitle()));
            } catch (MusicServiceException e) {
                usersTrackListsService.deleteUserTrackList(Long.parseLong(AccessToken.parseFromString(accessToken).getSub()), trackList);
            } catch (InternalErrorException ignored) {
            }
        }
        return ResponseEntity.ok(new UserTrackListsResponse(result));
    }

    @Override
    public ResponseEntity<UserTrackListResponse> addUserTrackList(String accessToken, String trackListUrl) throws TokenExpiredException, ParseTokenException, TokenBlacklistedException, MusicServiceException, InternalErrorException, UserNotFoundException, TrackListAlreadyAddedException {
        verifyAccessToken(accessToken);
        final TrackListDto res = musicService.getTrackListData(trackListUrl);
        usersTrackListsService.saveUserTrackList(Long.parseLong(AccessToken.parseFromString(accessToken).getSub()), trackListUrl);
        return ResponseEntity.ok(new UserTrackListResponse(new TrackListHeaderDto(res.getUrl(), res.getTitle())));
    }

    @Override
    public ResponseEntity<DetailResponse> deleteUserTrackList(String accessToken, String trackListUrl) throws ParseTokenException, UserNotFoundException, TrackListNotAddedException, MusicServiceException, InternalErrorException, TokenExpiredException, TokenBlacklistedException {
        verifyAccessToken(accessToken);
        usersTrackListsService.deleteUserTrackList(Long.parseLong(AccessToken.parseFromString(accessToken).getSub()), trackListUrl);
        return ResponseEntity.ok(new DetailResponse("Track succsessfully deleted"));
    }

    @Override
    public ResponseEntity<UserConcertsResponse> getUserConcerts(String accessToken) throws TokenExpiredException, ParseTokenException, TokenBlacklistedException, UserNotFoundException, InternalErrorException, TrackListNotAddedException {
        verifyAccessToken(accessToken);
        long userId = Long.parseLong(AccessToken.parseFromString(accessToken).getSub());
        final Optional<UserDto> optionalUser = usersService.findUser(userId);
        if (optionalUser.isEmpty()) {
            throw new UserNotFoundException();
        }
        List<String> userCities = usersCitiesService.getUserCities(optionalUser.get().getId());
        if (userCities.isEmpty()) {
            return ResponseEntity.ok(new UserConcertsResponse(new ArrayList<>()));
        }
        List<String> userTrackLists = usersTrackListsService.getUserTrackLists(userId);
        if (userTrackLists.isEmpty()) {
            return ResponseEntity.ok(new UserConcertsResponse(new ArrayList<>()));
        }
        HashSet<Integer> userArtists = new HashSet<>();

        for (String trackList : userTrackLists) {
            try {
                List<ArtistDto> artistDtoList = musicService.getTrackListData(trackList).getArtists();
                for (ArtistDto artist : artistDtoList) {
                    userArtists.add(artist.getYandexMusicId());
                }
            } catch (MusicServiceException e) {
                usersTrackListsService.deleteUserTrackList(userId, trackList);
            }
        }

        HashSet<String> userCitiesSet = new HashSet<>(userCities);
        List<ConcertDto> ret = new ArrayList<>();
        for (int artistId : userArtists) {
            try {
                List<ConcertDto> artistConcerts = musicService.getConcertsByArtistId(artistId);
                for (ConcertDto concert : artistConcerts) {
                    if (userCitiesSet.contains(concert.getCity())) {
                        ret.add(concert);
                        saveShownConcertNoException(userId, concert.getAfishaUrl());
                    }
                }
            }
            catch (MusicServiceException e) {
                log.warning(e.getMessage());
            }
        }

        return ResponseEntity.ok(new UserConcertsResponse(ret));
    }

    @Override
    public ResponseEntity<DetailResponse> putFirebaseToken(String accessToken, RefreshFirebaseTokenBodyModel refreshFirebaseTokenBodyModel) {
        return null;
    }

    private void saveShownConcertNoException(long telegramId, String concertUrl) {
        try {
            shownConcertsService.saveShownConcert(telegramId, concertUrl);
        } catch (Exception ignored) {
        }
    }

    private String generateCode(){
        return (new Date().getTime() % 1000) + RandomStringUtils.randomAlphabetic(3);
    }

    private void verifyAccessToken(String token) throws ParseTokenException, TokenExpiredException, TokenBlacklistedException {
        Jwts.parser().verifyWith(secretKey).build().isSigned(token);
        AccessToken accessToken = AccessToken.parseFromString(token);
        if(accessToken.getExp() < new Date().getTime()){
            throw new TokenExpiredException(token);
        }
        if(accessTokensService.isTokenBlacklisted(token)){
            throw new TokenBlacklistedException(token);
        }
    }

    private void verifyRefreshToken(String token) throws ParseTokenException, TokenExpiredException, TokenBlacklistedException {
        Jwts.parser().verifyWith(secretKey).build().isSigned(token);
        RefreshToken refreshToken = RefreshToken.parseFromString(token);
        if(refreshToken.getExp() < new Date().getTime()){
            throw new TokenExpiredException(token);
        }
        if(refreshTokensService.isTokenBlacklisted(token)){
            throw new TokenBlacklistedException(token);
        }
    }
}
