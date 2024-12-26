package ru.nsu.concert_mate.user_service.services.users.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import ru.nsu.concert_mate.user_service.model.dto.ShownConcertDto;
import ru.nsu.concert_mate.user_service.model.entities.ShownConcertEmbeddedEntity;
import ru.nsu.concert_mate.user_service.model.entities.ShownConcertEntity;
import ru.nsu.concert_mate.user_service.model.entities.UserEntity;
import ru.nsu.concert_mate.user_service.repositories.ShownConcertsRepository;
import ru.nsu.concert_mate.user_service.repositories.UsersRepository;
import ru.nsu.concert_mate.user_service.services.users.UsersShownConcertsService;
import ru.nsu.concert_mate.user_service.services.users.exceptions.InternalErrorException;
import ru.nsu.concert_mate.user_service.services.users.exceptions.ShownConcertAlreadyAddedException;
import ru.nsu.concert_mate.user_service.services.users.exceptions.ShownConcertNotFoundException;
import ru.nsu.concert_mate.user_service.services.users.exceptions.UserNotFoundException;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UsersShownConcertsServiceImpl implements UsersShownConcertsService {
    private final UsersRepository usersRepository;
    private final ShownConcertsRepository shownConcertsRepository;
    private final ModelMapper modelMapper;

    @Override
    public ShownConcertDto saveShownConcert(long telegramId, String concertUrl) throws UserNotFoundException, ShownConcertAlreadyAddedException, InternalErrorException {
        final Optional<UserEntity> foundUser = usersRepository.findByTelegramId(telegramId);
        if (foundUser.isEmpty()) {
            log.error("can't save shown concert because user with telegram id {} not found", telegramId);
            throw new UserNotFoundException();
        }

        final Optional<ShownConcertEntity> testUnique = shownConcertsRepository.findById(
                new ShownConcertEmbeddedEntity(foundUser.get().getId(), concertUrl)
        );
        if (testUnique.isPresent()) {
            log.warn("can't save shown concert because user with telegram id {} already has this concert {}", telegramId, concertUrl);
            throw new ShownConcertAlreadyAddedException();
        }

        final ShownConcertEntity shownConcertEntity = new ShownConcertEntity(
                foundUser.get().getId(),
                concertUrl
        );
        final ShownConcertEntity savingResult = shownConcertsRepository.save(shownConcertEntity);
        if (!savingResult.equals(shownConcertEntity)) {
            log.error("can't save {} to user with telegram id {}", concertUrl, telegramId);
            throw new InternalErrorException();
        }
        log.info("successfully added {} ot user {}", concertUrl, telegramId);
        return modelMapper.map(savingResult, ShownConcertDto.class);
    }

    @Override
    public ShownConcertDto deleteShownConcert(long telegramId, String concertUrl) throws UserNotFoundException, ShownConcertNotFoundException {
        final Optional<UserEntity> foundUser = usersRepository.findByTelegramId(telegramId);
        if (foundUser.isEmpty()) {
            log.error("can't delete shown concert because user with telegram id {} not found", telegramId);
            throw new UserNotFoundException();
        }

        final Optional<ShownConcertEntity> testUnique = shownConcertsRepository.findById(
                new ShownConcertEmbeddedEntity(foundUser.get().getId(), concertUrl)
        );
        if (testUnique.isEmpty()) {
            log.warn("can't delete shown concert because user with telegram id {} don't have this concert {}", telegramId, concertUrl);
            throw new ShownConcertNotFoundException();
        }

        final ShownConcertEntity userShownConcert = new ShownConcertEntity(foundUser.get().getId(), concertUrl);
        shownConcertsRepository.delete(userShownConcert);
        log.info("successfully deleted {} ot user {}", concertUrl, telegramId);
        return modelMapper.map(userShownConcert, ShownConcertDto.class);
    }

    @Override
    public List<String> getShownConcerts(long telegramId) throws UserNotFoundException, InternalErrorException {
        final Optional<UserEntity> foundUser = usersRepository.findByTelegramId(telegramId);
        if (foundUser.isEmpty()) {
            log.error("can't get shown concerts because user with telegram id {} not found", telegramId);
            throw new UserNotFoundException();
        }

        final var userShownConcerts = shownConcertsRepository.getUserShownConcerts(
                foundUser.get().getId()
        );
        if (userShownConcerts.isEmpty()) {
            log.warn("no shown concerts was found for user {}", telegramId);
            throw new InternalErrorException();
        }
        log.info("successfully found shown concerts for user {}", telegramId);
        return userShownConcerts.get();
    }

    @Override
    public boolean hasShownConcert(long telegramId, String concertUrl) throws UserNotFoundException, InternalErrorException {
        final Optional<UserEntity> foundUser = usersRepository.findByTelegramId(telegramId);
        if (foundUser.isEmpty()) {
            log.error("can't check shown concert because user with telegram id {} not found", telegramId);
            throw new UserNotFoundException();
        }

        final Optional<ShownConcertEntity> testUnique = shownConcertsRepository.findById(
                new ShownConcertEmbeddedEntity(foundUser.get().getId(), concertUrl)
        );
        log.info("shown concert successfully checked for user {}", telegramId);
        return testUnique.isPresent();
    }
}
