package ru.nsu.concert_mate.user_service.services.users.impl;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import ru.nsu.concert_mate.user_service.model.dto.UserTrackListDto;
import ru.nsu.concert_mate.user_service.model.entities.UserEntity;
import ru.nsu.concert_mate.user_service.model.entities.UserTrackListEmbeddedEntity;
import ru.nsu.concert_mate.user_service.model.entities.UserTrackListEntity;
import ru.nsu.concert_mate.user_service.repositories.UsersRepository;
import ru.nsu.concert_mate.user_service.repositories.UsersTrackListsRepository;
import ru.nsu.concert_mate.user_service.services.users.UsersTrackListsService;
import ru.nsu.concert_mate.user_service.services.users.exceptions.InternalErrorException;
import ru.nsu.concert_mate.user_service.services.users.exceptions.TrackListAlreadyAddedException;
import ru.nsu.concert_mate.user_service.services.users.exceptions.TrackListNotAddedException;
import ru.nsu.concert_mate.user_service.services.users.exceptions.UserNotFoundException;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UsersTrackListsServiceImpl implements UsersTrackListsService {
    private final UsersRepository usersRepository;
    private final UsersTrackListsRepository trackListsRepository;
    private final ModelMapper modelMapper;

    @Override
    public UserTrackListDto saveUserTrackList(long telegramId, String trackListUrl) throws UserNotFoundException, TrackListAlreadyAddedException, InternalErrorException {
        int indexSplit = trackListUrl.indexOf('?');
        if (indexSplit != -1) {
            trackListUrl = trackListUrl.substring(0, indexSplit);
        }
        final Optional<UserEntity> foundUser = usersRepository.findByTelegramId(telegramId);
        if (foundUser.isEmpty()) {
            throw new UserNotFoundException();
        }

        final Optional<UserTrackListEntity> testUnique = trackListsRepository.findById(
                new UserTrackListEmbeddedEntity(foundUser.get().getId(), trackListUrl)
        );
        if (testUnique.isPresent()) {
            throw new TrackListAlreadyAddedException();
        }

        final UserTrackListEntity userTrackList = new UserTrackListEntity(
                foundUser.get().getId(),
                trackListUrl
        );
        final UserTrackListEntity savingResult = trackListsRepository.save(userTrackList);
        if (!savingResult.equals(userTrackList)) {
            throw new InternalErrorException();
        }

        return modelMapper.map(savingResult, UserTrackListDto.class);
    }

    @Override
    public UserTrackListDto deleteUserTrackList(long telegramId, String cityName) throws UserNotFoundException, TrackListNotAddedException {
        final Optional<UserEntity> foundUser = usersRepository.findByTelegramId(telegramId);
        if (foundUser.isEmpty()) {
            throw new UserNotFoundException();
        }

        final Optional<UserTrackListEntity> testUnique = trackListsRepository.findById(
                new UserTrackListEmbeddedEntity(foundUser.get().getId(), cityName)
        );
        if (testUnique.isEmpty()) {
            throw new TrackListNotAddedException();
        }

        final UserTrackListEntity userTrackList = new UserTrackListEntity(foundUser.get().getId(), cityName);
        trackListsRepository.delete(userTrackList);

        return modelMapper.map(userTrackList, UserTrackListDto.class);
    }

    @Override
    public List<String> getUserTrackLists(long telegramId) throws UserNotFoundException, InternalErrorException {
        final Optional<UserEntity> foundUser = usersRepository.findByTelegramId(telegramId);
        if (foundUser.isEmpty()) {
            throw new UserNotFoundException();
        }

        final var userTrackLists = trackListsRepository.getUserTrackLists(
                foundUser.get().getId()
        );
        if (userTrackLists.isEmpty()) {
            throw new InternalErrorException();
        }

        return userTrackLists.get();
    }
}
