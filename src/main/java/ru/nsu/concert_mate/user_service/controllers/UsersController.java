package ru.nsu.concert_mate.user_service.controllers;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
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
    private final long ACCESS_TOKEN_EXPIRATION_TIME = 1000000;
    private final long REFRESH_TOKEN_EXPIRATION_TIME = 1000000;
    private String jwtSigningKey = "asdf";

    @Override
    public ResponseEntity<DetailResponse> emailLogin(LoginEmailFormModel loginEmailFormModel){
        String code = generateCode();
        var user = usersService.addUser(loginEmailFormModel.getEmail(), code);
        emailService.sendMail(loginEmailFormModel.getEmail(), "Concert Mate code", "Here is your authentication code: " + code);
        return ResponseEntity.ok(new DetailResponse("Email code sended"));
    }

    @Override
    public ResponseEntity<TokensResponse> loginWithEmailCode(LoginEmailCodeFormModel loginEmailCodeFormModel){
        Date now = new Date();
        var user = usersService.findByEmail(loginEmailCodeFormModel.getEmail());
        if(user.getCode() != loginEmailCodeFormModel.getCode()){

        }
        SecretKey key = Keys.hmacShaKeyFor(jwtSigningKey.getBytes(StandardCharsets.UTF_8));
        String accessToken = Jwts.builder()
                .id(String.valueOf(user.getId()))
                .issuedAt(now)
                .expiration(new Date(now.getTime() + ACCESS_TOKEN_EXPIRATION_TIME))
                .signWith(key)
                .compact();
        String refreshToken = Jwts.builder()
                .id(String.valueOf(user.getId()))
                .issuedAt(now)
                .expiration(new Date(now.getTime() + ACCESS_TOKEN_EXPIRATION_TIME))
                .signWith(key)
                .compact();
        TokensResponse tokensResponse = new TokensResponse(accessToken,refreshToken);
        return ResponseEntity.ok(tokensResponse);
    }


    @Override
    public ResponseEntity<DetailResponse> logout(String token, LogoutBodyModel logoutBodyModel) {
        return ResponseEntity.ok(new DetailResponse("sad"));
    }

    @Override
    public ResponseEntity<DetailResponse> refresh(RefreshTokenBodyModel refreshTokenBodyModel) {
        return null;
    }

    @Override
    public ResponseEntity<UserCitiesResponse> getUserCities(String token) {
//        try {
//            return ResponseEntity.ok(new UserCitiesResponse(usersCitiesService.getUserCities(telegramId)));
//        } catch (UserNotFoundException ignored) {
//            return null;
//        } catch (Exception ignored) {
//            return null;
//        }
        return null;
    }

    @Override
    public ResponseEntity<DetailResponse> addUserCity(String token, String cityName) {
//        String cityToAdd;
//        if (cityName == null && (lat == null || lon == null)) {
//            return new UserCityAddResponse(ApiResponseStatusCode.INVALID_COORDS);
//        }
//        if (cityName != null) {
//            try {
//                var res = citiesService.findCity(cityName);
//                if (res.getCode() == CitySearchByNameCode.SUCCESS) {
//                    cityToAdd = res.getOptions().get(0).getName();
//                } else if (res.getCode() == CitySearchByNameCode.FUZZY) {
//                    return new UserCityAddResponse(ApiResponseStatusCode.FUZZY_CITY, res.getOptions().get(0).getName());
//                } else if (res.getCode() == CitySearchByNameCode.NOT_FOUND) {
//                    return new UserCityAddResponse(ApiResponseStatusCode.INVALID_CITY);
//                } else {
//                    return new UserCityAddResponse(ApiResponseStatusCode.INTERNAL_ERROR);
//                }
//            } catch (CitiesServiceException exception) {
//                return new UserCityAddResponse(ApiResponseStatusCode.INTERNAL_ERROR);
//            }
//        } else {
//            try {
//                CitySearchByCoordsResult res = citiesService.findCity(new CoordsDto(lat, lon));
//                if (res.getCode() == CitySearchByCoordsCode.SUCCESS) {
//                    var city = res.getOptions().stream().max(Comparator.comparingInt(CityDto::getPopulation));
//                    if (city.isEmpty()) {
//                        return new UserCityAddResponse(ApiResponseStatusCode.INVALID_CITY);
//                    }
//                    cityToAdd = city.get().getName();
//                } else if (res.getCode() == CitySearchByCoordsCode.NOT_FOUND) {
//                    return new UserCityAddResponse(ApiResponseStatusCode.INVALID_CITY);
//                } else if (res.getCode() == CitySearchByCoordsCode.INVALID_COORDS) {
//                    return new UserCityAddResponse(ApiResponseStatusCode.INVALID_COORDS);
//                } else {
//                    return new UserCityAddResponse(ApiResponseStatusCode.INTERNAL_ERROR);
//                }
//            } catch (CitiesServiceException e) {
//                return new UserCityAddResponse(ApiResponseStatusCode.INTERNAL_ERROR);
//            }
//        }
//
//        try {
//            usersCitiesService.saveUserCity(telegramId, cityToAdd);
//            return new UserCityAddResponse(cityToAdd);
//        } catch (UserNotFoundException ignored) {
//            return new UserCityAddResponse(ApiResponseStatusCode.USER_NOT_FOUND);
//        } catch (CityAlreadyAddedException ignored) {
//            return new UserCityAddResponse(ApiResponseStatusCode.CITY_ALREADY_ADDED);
//        } catch (Exception ignored) {
//            return new UserCityAddResponse(ApiResponseStatusCode.INTERNAL_ERROR);
//        }
        return null;
    }

    @Override
    public ResponseEntity<DetailResponse>  deleteUserCity(String token, String cityName) {
//        try {
//            usersCitiesService.deleteUserCity(telegramId, cityName);
//            return new DefaultUsersApiResponse();
//        } catch (UserNotFoundException ignored) {
//            return new DefaultUsersApiResponse(ApiResponseStatusCode.USER_NOT_FOUND);
//        } catch (CityNotAddedException ignored) {
//            return new DefaultUsersApiResponse(ApiResponseStatusCode.CITY_NOT_ADDED);
//        } catch (Exception ignored) {
//            return new DefaultUsersApiResponse(ApiResponseStatusCode.INTERNAL_ERROR);
//        }
        return null;
    }

    @Override
    public ResponseEntity<UserTrackListsResponse> getUserTrackLists(String token) {
//        try {
//            final List<String> trackLists = usersTrackListsService.getUserTrackLists(telegramId);
//            final List<TrackListHeaderDto> result = new ArrayList<>();
//            for (String trackList : trackLists) {
//                try {
//                    TrackListDto trackListDto = musicService.getTrackListData(trackList);
//                    result.add(new TrackListHeaderDto(trackListDto.getUrl(), trackListDto.getTitle()));
//                } catch (MusicServiceException e) {
//                    usersTrackListsService.deleteUserTrackList(telegramId, trackList);
//                } catch (InternalErrorException ignored) {
//                }
//            }
//            return new UserTrackListsResponse(result);
//        } catch (UserNotFoundException ignored) {
//            return new UserTrackListsResponse(ApiResponseStatusCode.USER_NOT_FOUND);
//        } catch (Exception ignored) {
//            return new UserTrackListsResponse(ApiResponseStatusCode.INTERNAL_ERROR);
//        }
        return null;
    }

    @Override
    public ResponseEntity<UserTrackListResponse> addUserTrackList(String token, String trackListUrl) {
//        try {
//            final TrackListDto res = musicService.getTrackListData(trackListUrl);
//            usersTrackListsService.saveUserTrackList(telegramId, trackListUrl);
//            return new UserTrackListResponse(new TrackListHeaderDto(res.getUrl(), res.getTitle()));
//        } catch (UserNotFoundException ignored) {
//            return new UserTrackListResponse(ApiResponseStatusCode.USER_NOT_FOUND);
//        } catch (TrackListAlreadyAddedException ignored) {
//            return new UserTrackListResponse(ApiResponseStatusCode.TRACK_LIST_ALREADY_ADDED);
//        } catch (MusicServiceException ignored) {
//            return new UserTrackListResponse(ApiResponseStatusCode.INVALID_TRACK_LIST);
//        } catch (Exception ignored) {
//            return new UserTrackListResponse(ApiResponseStatusCode.INTERNAL_ERROR);
//        }
        return null;
    }

    @Override
    public ResponseEntity<DetailResponse> deleteUserTrackList(String token, String trackListUrl) {
//        try {
//            usersTrackListsService.deleteUserTrackList(telegramId, trackListUrl);
//            final TrackListDto trackListData = musicService.getTrackListData(trackListUrl);
//            return new UserTrackListResponse(new TrackListHeaderDto(trackListData.getUrl(), trackListData.getTitle()));
//        } catch (UserNotFoundException ignored) {
//            return new UserTrackListResponse(ApiResponseStatusCode.USER_NOT_FOUND);
//        } catch (TrackListNotAddedException ignored) {
//            return new UserTrackListResponse(ApiResponseStatusCode.TRACK_LIST_NOT_ADDED);
//        } catch (MusicServiceException ignored) {
//            return new UserTrackListResponse();
//        } catch (Exception ignored) {
//            return new UserTrackListResponse(ApiResponseStatusCode.INTERNAL_ERROR);
//        }
        return null;
    }

    @Override
    public ResponseEntity<UserConcertsResponse> getUserConcerts(String token) {
//        final Optional<UserDto> optionalUser = usersService.findUser(telegramId);
//        if (optionalUser.isEmpty()) {
//            return new UserConcertsResponse(ApiResponseStatusCode.USER_NOT_FOUND);
//        }
//        try {
//            List<String> userCities = usersCitiesService.getUserCities(optionalUser.get().getTelegramId());
//            if (userCities.isEmpty()) {
//                return new UserConcertsResponse();
//            }
//            List<String> userTrackLists = usersTrackListsService.getUserTrackLists(telegramId);
//            if (userTrackLists.isEmpty()) {
//                return new UserConcertsResponse();
//            }
//            HashSet<Integer> userArtists = new HashSet<>();
//
//
//            for (String trackList : userTrackLists) {
//                try {
//                    List<ArtistDto> artistDtoList = musicService.getTrackListData(trackList).getArtists();
//                    for (ArtistDto artist : artistDtoList) {
//                        userArtists.add(artist.getYandexMusicId());
//                    }
//                } catch (MusicServiceException e) {
//                    usersTrackListsService.deleteUserTrackList(telegramId, trackList);
//                }
//            }
//
//            HashSet<String> userCitiesSet = new HashSet<>(userCities);
//            List<ConcertDto> ret = new ArrayList<>();
//            for (int artistId : userArtists) {
//                try {
//                    List<ConcertDto> artistConcerts = musicService.getConcertsByArtistId(artistId);
//                    for (ConcertDto concert : artistConcerts) {
//                        if (userCitiesSet.contains(concert.getCity())) {
//                            ret.add(concert);
//                            saveShownConcertNoException(telegramId, concert.getAfishaUrl());
//                        }
//                    }
//                }
//                catch (MusicServiceException e) {
//                    log.warning(e.getMessage());
//                }
//            }
//
//            return new UserConcertsResponse(ret);
//        } catch (Exception ignored) {
//            return new UserConcertsResponse(ApiResponseStatusCode.INTERNAL_ERROR);
//        }
        return null;
    }

    @Override
    public ResponseEntity<DetailResponse> putFirebaseToken(String token, RefreshFirebaseTokenBodyModel refreshFirebaseTokenBodyModel) {
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
}
