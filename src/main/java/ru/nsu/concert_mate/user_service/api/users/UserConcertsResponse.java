package ru.nsu.concert_mate.user_service.api.users;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.nsu.concert_mate.user_service.model.dto.ConcertDto;

import java.util.List;

@Data
@AllArgsConstructor
public class UserConcertsResponse {
    private List<ConcertDto> concerts;
}
