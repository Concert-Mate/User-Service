package ru.nsu.concerts_mate.users_service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestTemplate;
import ru.nsu.concerts_mate.users_service.api.ApiResponseStatusCode;
import ru.nsu.concerts_mate.users_service.api.ApiResponseStatus;
import ru.nsu.concerts_mate.users_service.api.users.*;
import ru.nsu.concerts_mate.users_service.model.dto.TrackListHeaderDto;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

// TODO: add tests for adding invalid cities or invalid track-lists

@TestPropertySource("/application.yaml")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserServiceTest {
    private static class TelegramId {
        private static final AtomicLong id = new AtomicLong();

        public TelegramId() {
            id.incrementAndGet();
        }

        public long getId() {
            return id.get();
        }
    }

    private static class City {
        private static final AtomicLong cityIndex = new AtomicLong();

        public City() {
            cityIndex.incrementAndGet();
        }

        public String getCity() {
            return String.format("City-%d", cityIndex.get());
        }
    }

    private static class TrackList {
        private static final AtomicLong trackListIndex = new AtomicLong();

        public TrackList() {
            trackListIndex.incrementAndGet();
        }

        public String getUrl() {
            return String.format("URL-%d", trackListIndex.get());
        }
    }

    @LocalServerPort
    private int port;

    @Autowired
    JdbcTemplate jdbcTemplate;
    private String baseUrl = "http://localhost:";

    private RestTemplate restTemplate;

    @BeforeEach
    void setup() {
        restTemplate = new RestTemplate();
        baseUrl = baseUrl + port;
    }

    @Test
    void testAddUser() {
        addUserSuccess(new TelegramId().getId());
    }

    @Test
    void testAddUserTwice() {
        final long telegramId = new TelegramId().getId();
        addUserSuccess(telegramId);
        addUserAlreadyExists(telegramId);
    }

    @Test
    void testAddDeleteUser() {
        final long telegramId = new TelegramId().getId();
        addUserSuccess(telegramId);
        deleteUserSuccess(telegramId);
    }

    @Test
    void testDeleteUnknownUser() {
        final long telegramId = new TelegramId().getId();
        deleteUserNotFound(telegramId);
    }

    @Test
    void testAddDeleteTwiceUser() {
        final long telegramId = new TelegramId().getId();
        addUserSuccess(telegramId);
        deleteUserSuccess(telegramId);
        deleteUserNotFound(telegramId);
    }

    @Test
    void testAddUserCity() {
        final long telegramId = new TelegramId().getId();
        final String city = new City().getCity();
        addUserSuccess(telegramId);
        addUserCitySuccess(telegramId, city);
    }

    @Test
    void testAddUserCityAlreadyAdded() {
        final long telegramId = new TelegramId().getId();
        final String city = new City().getCity();
        addUserSuccess(telegramId);
        addUserCitySuccess(telegramId, city);
        addUserCityUserAlreadyAdded(telegramId, city);
    }

    @Test
    void testAddUnknownUserCity() {
        final long telegramId = new TelegramId().getId();
        final String city = new City().getCity();
        addUserCityUserNotFound(telegramId, city);
    }

    @Test
    void deleteUserCity() {
        final long telegramId = new TelegramId().getId();
        final String city = new City().getCity();
        addUserSuccess(telegramId);
        addUserCitySuccess(telegramId, city);
        deleteUserCitySuccess(telegramId, city);
    }

    @Test
    void deleteUserCityNotAdded() {
        final long telegramId = new TelegramId().getId();
        final String city = new City().getCity();
        addUserSuccess(telegramId);
        deleteUserCityNotAdded(telegramId, city);
    }

    @Test
    void deleteUnknownUserCity() {
        final long telegramId = new TelegramId().getId();
        final String city = new City().getCity();
        deleteUserCityUserNotFound(telegramId, city);
    }

    @Test
    void testGetZeroUserCities() {
        final long telegramId = new TelegramId().getId();
        addUserSuccess(telegramId);
        getUserCitiesSuccess(telegramId, new ArrayList<>());
    }

    @Test
    void testGetOneUserCity() {
        final long telegramId = new TelegramId().getId();
        final String city = new City().getCity();
        addUserSuccess(telegramId);
        addUserCitySuccess(telegramId, city);
        getUserCitiesSuccess(telegramId, List.of(city));
    }

    @Test
    void testGetSeveralUserCities() {
        final long telegramId = new TelegramId().getId();
        final int citiesCount = 10;
        final List<String> cities = new ArrayList<>(citiesCount);
        addUserSuccess(telegramId);
        for (int i = 0; i < citiesCount; i++) {
            final String city = new City().getCity();
            cities.add(city);
            addUserCitySuccess(telegramId, city);
        }
        getUserCitiesSuccess(telegramId, cities);
    }

    @Test
    void testGetUnknownUserCities() {
        final long telegramId = new TelegramId().getId();
        getUserCitiesUserNotFound(telegramId);
    }

    @Test
    void testGetAndDeleteUserCity() {
        final long telegramId = new TelegramId().getId();
        final String city = new City().getCity();
        addUserSuccess(telegramId);
        addUserCitySuccess(telegramId, city);
        getUserCitiesSuccess(telegramId, List.of(city));
        deleteUserCitySuccess(telegramId, city);
        getUserCitiesSuccess(telegramId, new ArrayList<>());
    }

    @Test
    void testAddUserTrackList() {
        final long telegramId = new TelegramId().getId();
        final String trackListUrl = new TrackList().getUrl();
        addUserSuccess(telegramId);
        addUserTrackList(telegramId, trackListUrl);
    }

    @Test
    void testAddUserTrackListAlreadyAdded() {
        final long telegramId = new TelegramId().getId();
        final String trackListUrl = new TrackList().getUrl();
        addUserSuccess(telegramId);
        addUserTrackListSuccess(telegramId, trackListUrl);
        addUserTrackListAlreadyAdded(telegramId, trackListUrl);
    }

    @Test
    void testAddUnknownUserTrackList() {
        final long telegramId = new TelegramId().getId();
        final String trackListUrl = new TrackList().getUrl();
        addUserTrackListUserNotFound(telegramId, trackListUrl);
    }

    @Test
    void deleteUserTrackList() {
        final long telegramId = new TelegramId().getId();
        final String trackListUrl = new TrackList().getUrl();
        addUserSuccess(telegramId);
        addUserTrackListSuccess(telegramId, trackListUrl);
        deleteUserTrackListSuccess(telegramId, trackListUrl);
    }

    @Test
    void deleteUserTrackListNotAdded() {
        final long telegramId = new TelegramId().getId();
        final String trackListUrl = new TrackList().getUrl();
        addUserSuccess(telegramId);
        deleteUserTrackListNotAdded(telegramId, trackListUrl);
    }

    @Test
    void deleteUnknownUserTrackList() {
        final long telegramId = new TelegramId().getId();
        final String trackListUrl = new TrackList().getUrl();
        deleteUserTrackListUserNotFound(telegramId, trackListUrl);
    }

    @Test
    void testGetZeroUserTrackLists() {
        final long telegramId = new TelegramId().getId();
        addUserSuccess(telegramId);
        getUserTrackListsSuccess(telegramId, new ArrayList<>());
    }

    @Test
    void testGetOneUserTrackList() {
        final long telegramId = new TelegramId().getId();
        final String trackListUrl = new TrackList().getUrl();
        addUserSuccess(telegramId);
        addUserTrackListSuccess(telegramId, trackListUrl);
        getUserTrackListsSuccess(telegramId, List.of(trackListUrl));
    }

    @Test
    void testGetSeveralUserTrackLists() {
        final long telegramId = new TelegramId().getId();
        final int trackListsCount = 10;
        final List<String> urls = new ArrayList<>(trackListsCount);
        addUserSuccess(telegramId);
        for (int i = 0; i < trackListsCount; i++) {
            final String url = new TrackList().getUrl();
            urls.add(url);
            addUserTrackListSuccess(telegramId, url);
        }
        getUserTrackListsSuccess(telegramId, urls);
    }

    @Test
    void testGetUnknownUserTrackLists() {
        final long telegramId = new TelegramId().getId();
        getUserTrackListsUserNotFound(telegramId);
    }

    @Test
    void testGetAndDeleteUserTrackList() {
        final long telegramId = new TelegramId().getId();
        final String trackListUrl = new TrackList().getUrl();
        addUserSuccess(telegramId);
        addUserTrackList(telegramId, trackListUrl);
        getUserTrackListsSuccess(telegramId, List.of(trackListUrl));
        deleteUserTrackListSuccess(telegramId, trackListUrl);
        getUserTrackListsSuccess(telegramId, new ArrayList<>());
    }

    @Test
    void testGetUserConcerts() {
        final long telegramId = new TelegramId().getId();
        addUserSuccess(telegramId);
        getUserConcertsSuccess(telegramId);
    }

    @Test
    void testGetUnknownUserConcerts() {
        final long telegramId = new TelegramId().getId();
        getUserConcertsUserNotFound(telegramId);
    }

    void addUserSuccess(long telegramId) {
        addUserWithCode(telegramId, ApiResponseStatusCode.SUCCESS);
    }

    void addUserAlreadyExists(long telegramId) {
        addUserWithCode(telegramId, ApiResponseStatusCode.USER_ALREADY_EXISTS);
    }

    void deleteUserSuccess(long telegramId) {
        deleteUserWithCode(telegramId, ApiResponseStatusCode.SUCCESS);
    }

    void deleteUserNotFound(long telegramId) {
        deleteUserWithCode(telegramId, ApiResponseStatusCode.USER_NOT_FOUND);
    }

    void addUserCitySuccess(long telegramId, String cityName) {
        addUserCityWithCode(telegramId, cityName, ApiResponseStatusCode.SUCCESS);
    }

    void addUserCityUserNotFound(long telegramId, String cityName) {
        addUserCityWithCode(telegramId, cityName, ApiResponseStatusCode.USER_NOT_FOUND);
    }

    void addUserCityUserAlreadyAdded(long telegramId, String cityName) {
        addUserCityWithCode(telegramId, cityName, ApiResponseStatusCode.CITY_ALREADY_ADDED);
    }

    void deleteUserCitySuccess(long telegramId, String cityName) {
        deleteUserCityWithCode(telegramId, cityName, ApiResponseStatusCode.SUCCESS);
    }

    void deleteUserCityUserNotFound(long telegramId, String cityName) {
        deleteUserCityWithCode(telegramId, cityName, ApiResponseStatusCode.USER_NOT_FOUND);
    }

    void deleteUserCityNotAdded(long telegramId, String cityName) {
        deleteUserCityWithCode(telegramId, cityName, ApiResponseStatusCode.CITY_NOT_ADDED);
    }

    void getUserCitiesSuccess(long telegramId, List<String> expectedCities) {
        final UserCitiesResponse response = getUserCitiesAndValidate(telegramId);
        matchResponseStatus(response.getStatus(), ApiResponseStatusCode.SUCCESS);
        final List<String> realCities = response.getCities();
        final Set<String> expectedCitiesSet = new HashSet<>(expectedCities);
        final Set<String> realCitiesSet = new HashSet<>(realCities);
        assertEquals(expectedCitiesSet, realCitiesSet);

        // Check that response doesn't contain city duplicates
        assertEquals(realCitiesSet.size(), realCities.size());
    }

    void getUserCitiesUserNotFound(long telegramId) {
        final UserCitiesResponse response = getUserCitiesAndValidate(telegramId);
        matchResponseStatus(response.getStatus(), ApiResponseStatusCode.USER_NOT_FOUND);
    }

    void addUserTrackListSuccess(long telegramId, String url) {
        addUserTrackListWithCode(telegramId, url, ApiResponseStatusCode.SUCCESS);
    }

    void addUserTrackListUserNotFound(long telegramId, String url) {
        addUserTrackListWithCode(telegramId, url, ApiResponseStatusCode.USER_NOT_FOUND);
    }

    void addUserTrackListAlreadyAdded(long telegramId, String url) {
        addUserTrackListWithCode(telegramId, url, ApiResponseStatusCode.TRACK_LIST_ALREADY_ADDED);
    }

    void deleteUserTrackListSuccess(long telegramId, String url) {
        deleteUserTrackListWithCode(telegramId, url, ApiResponseStatusCode.SUCCESS);
    }

    void deleteUserTrackListUserNotFound(long telegramId, String url) {
        deleteUserTrackListWithCode(telegramId, url, ApiResponseStatusCode.USER_NOT_FOUND);
    }

    void deleteUserTrackListNotAdded(long telegramId, String url) {
        deleteUserTrackListWithCode(telegramId, url, ApiResponseStatusCode.TRACK_LIST_NOT_ADDED);
    }

    void getUserTrackListsSuccess(long telegramId, List<String> expectedTrackLists) {
        final UserTrackListsResponse response = getUserTrackListsAndValidate(telegramId);
        matchResponseStatus(response.getStatus(), ApiResponseStatusCode.SUCCESS);
        final List<TrackListHeaderDto> realTrackLists = response.getTrackLists();
        final Set<String> expectedTrackListsSet = new HashSet<>(expectedTrackLists);
        final Set<String> realTrackListsSet = realTrackLists.stream().map(TrackListHeaderDto::getUrl).collect(Collectors.toSet());

        assertEquals(expectedTrackListsSet, realTrackListsSet);

        // Check that response doesn't contain track-lists duplicates
        assertEquals(realTrackListsSet.size(), realTrackLists.size());
    }

    void getUserTrackListsUserNotFound(long telegramId) {
        final UserTrackListsResponse response = getUserTrackListsAndValidate(telegramId);
        matchResponseStatus(response.getStatus(), ApiResponseStatusCode.USER_NOT_FOUND);
    }

    void getUserConcertsSuccess(long telegramId) {
        final UserConcertsResponse response = getUserConcertsAndValidate(telegramId);
        matchResponseStatus(response.getStatus(), ApiResponseStatusCode.SUCCESS);
    }

    void getUserConcertsUserNotFound(long telegramId) {
        final UserConcertsResponse response = getUserConcertsAndValidate(telegramId);
        matchResponseStatus(response.getStatus(), ApiResponseStatusCode.USER_NOT_FOUND);
    }

    UserCitiesResponse getUserCitiesAndValidate(long telegramId) {
        final ResponseEntity<UserCitiesResponse> responseEntity = getUserCities(telegramId);
        final UserCitiesResponse response = responseEntity.getBody();
        assertNotNull(response);
        final ApiResponseStatus status = response.getStatus();
        assertNotNull(status);
        assertNotNull(response.getCities());
        return response;
    }

    UserTrackListsResponse getUserTrackListsAndValidate(long telegramId) {
        final ResponseEntity<UserTrackListsResponse> responseEntity = getUserTrackLists(telegramId);
        final UserTrackListsResponse response = responseEntity.getBody();
        assertNotNull(response);
        final ApiResponseStatus status = response.getStatus();
        assertNotNull(status);
        assertNotNull(response.getTrackLists());
        return response;
    }

    UserConcertsResponse getUserConcertsAndValidate(long telegramId) {
        final ResponseEntity<UserConcertsResponse> responseEntity = getUserConcerts(telegramId);
        final UserConcertsResponse response = responseEntity.getBody();
        assertNotNull(response);
        final ApiResponseStatus status = response.getStatus();
        assertNotNull(status);
        assertNotNull(response.getConcerts());
        return response;
    }

    void addUserWithCode(long telegramId, ApiResponseStatusCode code) {
        final DefaultUsersApiResponse response = validateResponseEntity(addUser(telegramId));
        matchResponseStatus(response.getStatus(), code);
    }

    void deleteUserWithCode(long telegramId, ApiResponseStatusCode code) {
        final DefaultUsersApiResponse response = validateResponseEntity(deleteUser(telegramId));
        matchResponseStatus(response.getStatus(), code);
    }

    void addUserCityWithCode(long telegramId, String city, ApiResponseStatusCode code) {
        final DefaultUsersApiResponse response = validateResponseEntity(addUserCity(telegramId, city));
        matchResponseStatus(response.getStatus(), code);
    }

    void deleteUserCityWithCode(long telegramId, String city, ApiResponseStatusCode code) {
        final DefaultUsersApiResponse response = validateResponseEntity(deleteUserCity(telegramId, city));
        matchResponseStatus(response.getStatus(), code);
    }

    void addUserTrackListWithCode(long telegramId, String url, ApiResponseStatusCode code) {
        final DefaultUsersApiResponse response = validateResponseEntity(addUserTrackList(telegramId, url));
        matchResponseStatus(response.getStatus(), code);
    }

    void deleteUserTrackListWithCode(long telegramId, String url, ApiResponseStatusCode code) {
        final DefaultUsersApiResponse response = validateResponseEntity(deleteUserTrackList(telegramId, url));
        matchResponseStatus(response.getStatus(), code);
    }

    DefaultUsersApiResponse validateResponseEntity(ResponseEntity<DefaultUsersApiResponse> responseEntity) {
        final DefaultUsersApiResponse response = responseEntity.getBody();
        assertNotNull(response);
        final ApiResponseStatus status = response.getStatus();
        assertNotNull(status);
        return response;
    }

    void matchResponseStatus(ApiResponseStatus status, ApiResponseStatusCode code) {
        assertEquals(code.ordinal(), status.getCode());
        assertEquals(code.name(), status.getMessage());
        assertEquals(code == ApiResponseStatusCode.SUCCESS, status.isSuccess());
    }

    ResponseEntity<DefaultUsersApiResponse> addUser(long telegramId) {
        return post(
                getUserUrl(telegramId),
                DefaultUsersApiResponse.class
        );
    }

    ResponseEntity<DefaultUsersApiResponse> deleteUser(long telegramId) {
        return delete(
                getUserUrl(telegramId),
                DefaultUsersApiResponse.class
        );
    }

    ResponseEntity<DefaultUsersApiResponse> addUserCity(long telegramId, String city) {
        return post(
                getUserCityUrl(telegramId, city),
                DefaultUsersApiResponse.class
        );
    }

    ResponseEntity<DefaultUsersApiResponse> deleteUserCity(long telegramId, String city) {
        return delete(
                getUserCityUrl(telegramId, city),
                DefaultUsersApiResponse.class
        );
    }

    ResponseEntity<UserCitiesResponse> getUserCities(long telegramId) {
        return get(
                getUserCitiesUrl(telegramId),
                UserCitiesResponse.class
        );
    }

    ResponseEntity<DefaultUsersApiResponse> addUserTrackList(long telegramId, String trackListUrl) {
        return post(
                getUserTrackListUrl(telegramId, trackListUrl),
                DefaultUsersApiResponse.class
        );
    }

    ResponseEntity<DefaultUsersApiResponse> deleteUserTrackList(long telegramId, String trackListUrl) {
        return delete(
                getUserTrackListUrl(telegramId, trackListUrl),
                DefaultUsersApiResponse.class
        );
    }

    ResponseEntity<UserTrackListsResponse> getUserTrackLists(long telegramId) {
        return get(
                getUserTrackListsUrl(telegramId),
                UserTrackListsResponse.class
        );
    }

    ResponseEntity<UserConcertsResponse> getUserConcerts(long telegramId) {
        return get(
                getUserConcertsUrl(telegramId),
                UserConcertsResponse.class
        );
    }

    <T> ResponseEntity<T> post(String url, Class<T> clazz) {
        return restTemplate.postForEntity(
                url,
                null,
                clazz
        );
    }

    <T> ResponseEntity<T> delete(String url, Class<T> clazz) {
        return restTemplate.exchange(
                url,
                HttpMethod.DELETE,
                null,
                clazz
        );
    }

    <T> ResponseEntity<T> get(String url, Class<T> clazz) {
        return restTemplate.getForEntity(
                url,
                clazz
        );
    }

    private String getUserUrl(long telegramId) {
        return baseUrl + String.format("/users/%s", telegramId);
    }

    private String getUserCitiesUrl(long telegramId) {
        return getUserUrl(telegramId) + "/cities";
    }

    private String getUserCityUrl(long telegramId, String cityName) {
        return getUserCitiesUrl(telegramId) + String.format("?city=%s", cityName);
    }

    private String getUserTrackListsUrl(long telegramId) {
        return getUserUrl(telegramId) + "/track-lists";
    }

    private String getUserTrackListUrl(long telegramId, String url) {
        return getUserTrackListsUrl(telegramId) + String.format("?url=%s", url);
    }

    private String getUserConcertsUrl(long telegramId) {
        return getUserUrl(telegramId) + "/concerts";
    }
}
