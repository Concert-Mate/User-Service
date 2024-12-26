package ru.nsu.concert_mate.user_service.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import ru.nsu.concert_mate.user_service.model.dto.ArtistDto;
import ru.nsu.concert_mate.user_service.model.dto.ConcertDto;
import ru.nsu.concert_mate.user_service.model.dto.UserDto;
import ru.nsu.concert_mate.user_service.services.broker.BrokerEvent;
import ru.nsu.concert_mate.user_service.services.broker.BrokerException;
import ru.nsu.concert_mate.user_service.services.broker.BrokerService;
import ru.nsu.concert_mate.user_service.services.music.MusicService;
import ru.nsu.concert_mate.user_service.services.music.exceptions.MusicServiceException;
import ru.nsu.concert_mate.user_service.services.users.UsersCitiesService;
import ru.nsu.concert_mate.user_service.services.users.UsersService;
import ru.nsu.concert_mate.user_service.services.users.UsersShownConcertsService;
import ru.nsu.concert_mate.user_service.services.users.UsersTrackListsService;
import ru.nsu.concert_mate.user_service.services.users.exceptions.InternalErrorException;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class ConcertsScheduler {
    private final UsersService usersService;
    private final UsersCitiesService usersCitiesService;
    private final UsersTrackListsService usersTrackListsService;
    private final MusicService musicService;
    private final UsersShownConcertsService shownConcertsService;
    private final BrokerService brokerService;

    private void fillArtistsForUsers(List<String> trackLists, Map<Integer, List<UserDto>> artistsForUsers, UserDto user) {
        for (String trackList : trackLists) {
            try {
                List<ArtistDto> artists = musicService.getTrackListData(trackList).getArtists();
                for (ArtistDto artist : artists) {
                    var mapItem = artistsForUsers.computeIfAbsent(artist.getYandexMusicId(), k -> new ArrayList<>());
                    mapItem.add(user);
                }
            } catch (InternalErrorException ignored) {
                log.error("can't get info for {} track list", trackList);
            } catch (MusicServiceException e) {
                log.warn("track list {} is invalid", trackList);
                log.info("deleting track list {} for user {}", trackList, user.getTelegramId());
                deleteUserPlayListNoExcept(user.getTelegramId(), trackList);
            }
        }
    }
    //this method is used only to delete invalid track lists
    private void deleteUserPlayListNoExcept(long telegramId, String playListUrl) {
        try {
            usersTrackListsService.deleteUserTrackList(telegramId, playListUrl);
        } catch (Exception ignored) {
            log.warn("can't delete invalid track list {}", playListUrl);
        }
    }

    private void sendConcertToUser(List<ConcertDto> concerts, UserDto user) {
        try {
            brokerService.sendEvent(new BrokerEvent(user, concerts));
        } catch (BrokerException ignored) {
            log.error("can't send notification {} to user {}", concerts, user);
        }

        for (ConcertDto concert : concerts) {
            try {
                shownConcertsService.saveShownConcert(user.getTelegramId(), concert.getAfishaUrl());
            } catch (Exception ignored) {
                log.error("can't save notification {} for user {}", concert, user);
            }
        }
    }

    private boolean isConcertSent(ConcertDto concert, UserDto user) {
        try {
            return shownConcertsService.hasShownConcert(user.getTelegramId(), concert.getAfishaUrl());
        } catch (Exception e) {
            log.warn("can't determine if concert notification is already send");
            return false;
        }
    }

    @Scheduled(fixedRateString = "${spring.scheduler.fixed_rate}")
    public void updateConcerts() {
        final List<UserDto> users = usersService.findAllUsers();
        final Map<Integer, List<UserDto>> artistsForUsers = new HashMap<>();
        final Map<Long, List<String>> citiesForUsers = new HashMap<>();

        for (UserDto user : users) {
            try {
                final List<String> userCities = usersCitiesService.getUserCities(user.getTelegramId());
                citiesForUsers.put(user.getTelegramId(), userCities);
            } catch (Exception ignored) {
                log.error("can't get cities for user {}", user);
                continue;
            }
            try {
                final List<String> trackLists = usersTrackListsService.getUserTrackLists(user.getTelegramId());
                fillArtistsForUsers(trackLists, artistsForUsers, user);
            } catch (Exception ignored) {
                log.error("can't get track lists for user {}", user);
            }
        }

        final Map<UserDto, List<ConcertDto>> concertsForUsers = new HashMap<>();

        for (Map.Entry<Integer, List<UserDto>> entry : artistsForUsers.entrySet()) {
            try {
                final List<ConcertDto> concerts = musicService.getConcertsByArtistId(entry.getKey());
                for (ConcertDto concert : concerts) {
                    for (UserDto user : entry.getValue()) {
                        final List<String> userCities = citiesForUsers.get(user.getTelegramId());
                        if (userCities.contains(concert.getCity()) && !isConcertSent(concert, user)) {
                            var mapItem = concertsForUsers.computeIfAbsent(user, c -> new ArrayList<>());
                            mapItem.add(concert);
                        }
                    }
                }
            } catch (Exception ignored) {
               log.error("can't get concerts for artist with id {}", entry.getKey());
            }
        }

        for (Map.Entry<UserDto, List<ConcertDto>> entry : concertsForUsers.entrySet()) {
            sendConcertToUser(entry.getValue(), entry.getKey());
        }

        log.info("scheduling is finished");
    }
}
