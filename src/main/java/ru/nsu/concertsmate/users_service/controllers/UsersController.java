package ru.nsu.concertsmate.users_service.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import ru.nsu.concertsmate.users_service.api.users.*;
import ru.nsu.concertsmate.users_service.model.dto.UserDto;
import ru.nsu.concertsmate.users_service.services.CitiesService;
import ru.nsu.concertsmate.users_service.services.TracksListsService;
import ru.nsu.concertsmate.users_service.services.UsersService;
import ru.nsu.concertsmate.users_service.services.exceptions.*;

@RestController()
public class UsersController implements UsersApi {
    private final UsersService usersService;

    private final CitiesService citiesService;

    private final TracksListsService tracksListsService;

    @Autowired
    public UsersController(UsersService usersService, CitiesService citiesService, TracksListsService tracksListsService) {
        this.usersService = usersService;
        this.citiesService = citiesService;
        this.tracksListsService = tracksListsService;
    }

    @Override
    public UsersApiResponse addUser(long telegramId) {
        try {
            final UserDto user = usersService.findUser(telegramId);
            if (user != null) {
                return new UsersApiResponse(UsersApiResponseStatusCode.USER_ALREADY_EXISTS);
            }
            usersService.addUser(telegramId);
            return new UsersApiResponse();
        } catch (Exception e) {
            return new UsersApiResponse(UsersApiResponseStatusCode.INTERNAL_ERROR);
        }
    }

    @Override
    public UsersApiResponse deleteUser(long telegramId) {
        try {
            var user = usersService.deleteUser(telegramId);
            if (user == null) {
                return new UsersApiResponse(UsersApiResponseStatusCode.USER_NOT_FOUND);
            }
            return new UsersApiResponse();
        } catch (Exception e) {
            return new UsersApiResponse(UsersApiResponseStatusCode.INTERNAL_ERROR);
        }
    }

    @Override
    public UserCitiesResponse getUserCities(long telegramId) {
        try {
            return new UserCitiesResponse(citiesService.getUserCities(telegramId));
        } catch (UserNotFoundException e) {
            return new UserCitiesResponse(UsersApiResponseStatusCode.USER_NOT_FOUND);
        } catch (Exception e) {
            return new UserCitiesResponse(UsersApiResponseStatusCode.INTERNAL_ERROR);
        }
    }

    @Override
    public UsersApiResponse addUserCity(long telegramId, String cityName) {
        try {
            citiesService.saveUserCity(telegramId, cityName);
            return new UsersApiResponse();
        } catch (UserNotFoundException e) {
            return new UsersApiResponse(UsersApiResponseStatusCode.USER_NOT_FOUND);
        } catch (CityAlreadyAddedException e) {
            return new UsersApiResponse(UsersApiResponseStatusCode.CITY_ALREADY_ADDED);
        } catch (Exception e) {
            return new UsersApiResponse(UsersApiResponseStatusCode.INTERNAL_ERROR);
        }
    }

    @Override
    public UsersApiResponse deleteUserCity(long telegramId, String cityName) {
        try {
            citiesService.deleteUserCity(telegramId, cityName);
            return new UsersApiResponse();
        } catch (UserNotFoundException e) {
            return new UsersApiResponse(UsersApiResponseStatusCode.USER_NOT_FOUND);
        } catch (CityNotAddedException e) {
            return new UsersApiResponse(UsersApiResponseStatusCode.CITY_NOT_ADDED);
        } catch (Exception e) {
            return new UsersApiResponse(UsersApiResponseStatusCode.INTERNAL_ERROR);
        }
    }

    @Override
    public UserTracksListsResponse getUserTracksLists(long telegramId) {
        try {
            return new UserTracksListsResponse(tracksListsService.getUserTracksLists(telegramId));
        } catch (UserNotFoundException e) {
            return new UserTracksListsResponse(UsersApiResponseStatusCode.USER_NOT_FOUND);
        } catch (Exception e) {
            return new UserTracksListsResponse(UsersApiResponseStatusCode.INTERNAL_ERROR);
        }
    }

    @Override
    public UsersApiResponse addUserTracksList(long telegramId, String tracksListURL) {
        try {
            tracksListsService.saveUserTracksList(telegramId, tracksListURL);
            return new UsersApiResponse();
        } catch (UserNotFoundException e) {
            return new UsersApiResponse(UsersApiResponseStatusCode.USER_NOT_FOUND);
        } catch (TracksListAlreadyAddedException e) {
            return new UsersApiResponse(UsersApiResponseStatusCode.TRACKS_LIST_ALREADY_ADDED);
        } catch (Exception e) {
            return new UsersApiResponse(UsersApiResponseStatusCode.INTERNAL_ERROR);
        }
    }

    @Override
    public UsersApiResponse deleteUserTracksList(long telegramId, String tracksListURL) {
        try {
            tracksListsService.deleteUserTracksList(telegramId, tracksListURL);
            return new UsersApiResponse();
        } catch (UserNotFoundException e) {
            return new UsersApiResponse(UsersApiResponseStatusCode.USER_NOT_FOUND);
        } catch (TracksListNotAddedException e) {
            return new UsersApiResponse(UsersApiResponseStatusCode.TRACKS_LIST_NOT_ADDED);
        } catch (Exception e) {
            return new UsersApiResponse(UsersApiResponseStatusCode.INTERNAL_ERROR);
        }
    }

    @Override
    public UserConcertsResponse getUserConcerts(long telegramId) {
        try {
            final UserDto user = usersService.findUser(telegramId);
            if (user != null) {
                return new UserConcertsResponse(UsersApiResponseStatusCode.SUCCESS);
            }
            return new UserConcertsResponse(UsersApiResponseStatusCode.USER_NOT_FOUND);
        } catch (Exception e) {
            return new UserConcertsResponse(UsersApiResponseStatusCode.INTERNAL_ERROR);
        }
    }
}
