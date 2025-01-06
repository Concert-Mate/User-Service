package ru.nsu.concert_mate.user_service.api.users;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.nsu.concert_mate.user_service.model.dto.TrackListHeaderDto;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserTrackListsResponse {
    @JsonProperty(value = "track_lists")
    private List<TrackListHeaderDto> trackLists;
}
