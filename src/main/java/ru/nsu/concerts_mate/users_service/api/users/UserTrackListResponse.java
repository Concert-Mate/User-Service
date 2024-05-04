package ru.nsu.concerts_mate.users_service.api.users;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import ru.nsu.concerts_mate.users_service.api.ApiResponseStatus;
import ru.nsu.concerts_mate.users_service.api.ApiResponseStatusCode;
import ru.nsu.concerts_mate.users_service.model.dto.TrackListHeaderDto;

@Data
public class UserTrackListResponse {
    private ApiResponseStatus status;

    @JsonProperty(value = "tracks_list")
    private TrackListHeaderDto trackList;

    public UserTrackListResponse() {
        this(ApiResponseStatusCode.SUCCESS, null);
    }

    public UserTrackListResponse(ApiResponseStatusCode code) {
        this(code, null);
    }

    public UserTrackListResponse(TrackListHeaderDto trackList) {
        this(ApiResponseStatusCode.SUCCESS, trackList);
    }

    public UserTrackListResponse(ApiResponseStatusCode code, TrackListHeaderDto trackList) {
        this.status = new ApiResponseStatus(code);
        this.trackList = trackList;
    }
}