package ru.nsu.concert_mate.user_service.services.users.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class UsersTrackListsServiceImpl implements UsersTrackListsService {
    private final UsersRepository usersRepository;
    private final UsersTrackListsRepository trackListsRepository;
    private final ModelMapper modelMapper;

    @Override
    public UserTrackListDto saveUserTrackList(long telegramId, String trackListUrl) throws UserNotFoundException, TrackListAlreadyAddedException, InternalErrorException {
        final Optional<UserEntity> foundUser = usersRepository.findByTelegramId(telegramId);
        if (foundUser.isEmpty()) {
            log.error("can't save track list because user with telegram id {} not found", telegramId);
            throw new UserNotFoundException();
        }

        final Optional<UserTrackListEntity> testUnique = trackListsRepository.findById(
                new UserTrackListEmbeddedEntity(foundUser.get().getId(), trackListUrl)
        );
        if (testUnique.isPresent()) {
            log.warn("can't save track list because user with telegram id {} already has track list{}", telegramId, trackListUrl);
            throw new TrackListAlreadyAddedException();
        }

        final UserTrackListEntity userTrackList = new UserTrackListEntity(
                foundUser.get().getId(),
                trackListUrl
        );
        final UserTrackListEntity savingResult = trackListsRepository.save(userTrackList);
        if (!savingResult.equals(userTrackList)) {
            log.error("can't save {} to user with telegram id {}", trackListUrl, telegramId);
            throw new InternalErrorException();
        }
        log.info("successfully added {} ot user {}", trackListUrl, telegramId);
        return modelMapper.map(savingResult, UserTrackListDto.class);
    }

    @Override
    public UserTrackListDto deleteUserTrackList(long telegramId, String trackListUrl) throws UserNotFoundException, TrackListNotAddedException {
        final Optional<UserEntity> foundUser = usersRepository.findByTelegramId(telegramId);
        if (foundUser.isEmpty()) {
            log.error("can't delete track list because user with telegram id {} not found", telegramId);
            throw new UserNotFoundException();
        }

        final Optional<UserTrackListEntity> testUnique = trackListsRepository.findById(
                new UserTrackListEmbeddedEntity(foundUser.get().getId(), trackListUrl)
        );
        if (testUnique.isEmpty()) {
            log.warn("can't delete track list because user with telegram id {} don't have city {}", telegramId, trackListUrl);
            throw new TrackListNotAddedException();
        }

        final UserTrackListEntity userTrackList = new UserTrackListEntity(foundUser.get().getId(), trackListUrl);
        trackListsRepository.delete(userTrackList);
        log.info("successfully deleted {} ot user {}", trackListUrl, telegramId);
        return modelMapper.map(userTrackList, UserTrackListDto.class);
    }

    @Override
    public List<String> getUserTrackLists(long telegramId) throws UserNotFoundException, InternalErrorException {
        final Optional<UserEntity> foundUser = usersRepository.findByTelegramId(telegramId);
        if (foundUser.isEmpty()) {
            log.error("can't get track lists because user with telegram id {} not found", telegramId);
            throw new UserNotFoundException();
        }

        final var userTrackLists = trackListsRepository.getUserTrackLists(
                foundUser.get().getId()
        );
        if (userTrackLists.isEmpty()) {
            log.warn("no track lists was found for user {}", telegramId);
            throw new InternalErrorException();
        }
        log.info("successfully found track lists for user {}", telegramId);
        return userTrackLists.get();
    }
}
