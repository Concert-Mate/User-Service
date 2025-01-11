package ru.nsu.concert_mate.user_service.services.music.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import ru.nsu.concert_mate.user_service.model.dto.ConcertDto;
import ru.nsu.concert_mate.user_service.model.dto.TrackListDto;
import ru.nsu.concert_mate.user_service.services.music.MusicService;
import ru.nsu.concert_mate.user_service.services.music.exceptions.ArtistNotFoundException;
import ru.nsu.concert_mate.user_service.services.music.exceptions.MusicServiceException;
import ru.nsu.concert_mate.user_service.services.music.exceptions.TrackListNotFoundException;
import ru.nsu.concert_mate.user_service.services.users.exceptions.InternalErrorException;

import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class MusicServiceImpl implements MusicService {
    // TODO: сделать нормально после релиза
    enum ErrorCodes {
        SUCCESS, INTERNAL_ERROR, ARTIST_NOT_FOUND, TRACK_LIST_NOT_FOUND
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    private static class ResponseStatusDTO {
        private int code;

        private String message;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    private static class ResponseMusicServicePlayListDTO {
        private ResponseStatusDTO status;

        @JsonProperty(value = "track_list")
        private TrackListDto trackList;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    private static class ResponseMusicServiceConcertsDTO {
        private ResponseStatusDTO status;

        private List<ConcertDto> concerts;
    }

    @Value("${spring.music-service.host}")
    private String serviceHost;

    @Value("${spring.music-service.port}")
    private int servicePort;

    @Value("${spring.music-service.scheme}")
    private String serviceScheme;

    private final RestTemplate restTemplate;

    @Autowired
    public MusicServiceImpl(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    @Override
    public TrackListDto getTrackListData(String url) throws InternalErrorException, MusicServiceException {
        final String serviceUrl = "%s://%s:%d/track-lists?url=%s".formatted(
                serviceScheme,
                serviceHost,
                servicePort,
                url
        );

        try {
            final ResponseEntity<ResponseMusicServicePlayListDTO> res = restTemplate.getForEntity(
                    serviceUrl,
                    ResponseMusicServicePlayListDTO.class
            );
            if (res.getStatusCode() == HttpStatus.UNPROCESSABLE_ENTITY || !res.hasBody()) {
                log.error("invalid Music-Service request or response for {}", serviceUrl);
                throw new InternalErrorException();
            }

            if (Objects.requireNonNull(res.getBody()).status.code == ErrorCodes.SUCCESS.ordinal()) {
                log.info("service successfully answered {}", Objects.requireNonNull(res.getBody()).trackList);
                return Objects.requireNonNull(res.getBody()).trackList;
            } else if (Objects.requireNonNull(res.getBody()).status.code == ErrorCodes.TRACK_LIST_NOT_FOUND.ordinal()) {
                log.info("tracklist not found {}", url);
                throw new TrackListNotFoundException(Objects.requireNonNull(res.getBody()).status.message);
            }
            log.warn("invalid status code in answer {}", res.getBody().status);
            throw new InternalErrorException();
        } catch (RestClientException e) {
            log.error("error during making request {}: {}", serviceUrl, e.getLocalizedMessage());
            throw new InternalErrorException();
        }
    }

    @Override
    public List<ConcertDto> getConcertsByArtistId(int artistId) throws InternalErrorException, MusicServiceException {
        final String serviceUrl = "%s://%s:%d/concerts?artist_id=%d".formatted(
                serviceScheme,
                serviceHost,
                servicePort,
                artistId
        );

        try {
            final ResponseEntity<ResponseMusicServiceConcertsDTO> res = restTemplate.getForEntity(
                    serviceUrl,
                    ResponseMusicServiceConcertsDTO.class
            );
            if (res.getStatusCode() == HttpStatus.UNPROCESSABLE_ENTITY || !res.hasBody()) {
                log.error("invalid Music-Service request or response for {}", serviceUrl);
                throw new InternalErrorException();
            }

            if (Objects.requireNonNull(res.getBody()).status.code == ErrorCodes.SUCCESS.ordinal()) {
                log.info("service successfully answered {}", Objects.requireNonNull(res.getBody()).concerts);
                return Objects.requireNonNull(res.getBody()).concerts;
            } else if (Objects.requireNonNull(res.getBody()).status.code == ErrorCodes.ARTIST_NOT_FOUND.ordinal()) {
                log.info("artist concerts not found for {}", artistId);
                throw new ArtistNotFoundException(Objects.requireNonNull(res.getBody()).status.message);
            }
            log.warn("invalid status code in answer {}", res.getBody().status);
            throw new InternalErrorException();
        } catch (RestClientException e) {
            log.error("error during making request {}: {}", serviceUrl, e.getLocalizedMessage());
            throw new InternalErrorException();
        }
    }
}
