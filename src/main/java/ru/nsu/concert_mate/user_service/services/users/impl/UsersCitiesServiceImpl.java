package ru.nsu.concert_mate.user_service.services.users.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import ru.nsu.concert_mate.user_service.model.dto.UserCityDto;
import ru.nsu.concert_mate.user_service.model.entities.UserCityEmbeddedEntity;
import ru.nsu.concert_mate.user_service.model.entities.UserCityEntity;
import ru.nsu.concert_mate.user_service.model.entities.UserEntity;
import ru.nsu.concert_mate.user_service.repositories.UsersCitiesRepository;
import ru.nsu.concert_mate.user_service.repositories.UsersRepository;
import ru.nsu.concert_mate.user_service.services.users.UsersCitiesService;
import ru.nsu.concert_mate.user_service.services.users.exceptions.CityAlreadyAddedException;
import ru.nsu.concert_mate.user_service.services.users.exceptions.CityNotAddedException;
import ru.nsu.concert_mate.user_service.services.users.exceptions.InternalErrorException;
import ru.nsu.concert_mate.user_service.services.users.exceptions.UserNotFoundException;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UsersCitiesServiceImpl implements UsersCitiesService {
    private final UsersRepository usersRepository;
    private final UsersCitiesRepository usersCitiesRepository;
    private final ModelMapper modelMapper;

    @Override
    public UserCityDto saveUserCity(long telegramId, String cityName) throws UserNotFoundException, CityAlreadyAddedException, InternalErrorException {
        final Optional<UserEntity> foundUser = usersRepository.findByTelegramId(telegramId);
        if (foundUser.isEmpty()) {
            log.error("can't save city because user with telegram id {} not found", telegramId);
            throw new UserNotFoundException();
        }

        final Optional<UserCityEntity> testUnique = usersCitiesRepository.findById(
                new UserCityEmbeddedEntity(foundUser.get().getId(), cityName)
        );
        if (testUnique.isPresent()) {
            log.warn("can't save city because user with telegram id {} already has city {}", telegramId, cityName);
            throw new CityAlreadyAddedException();
        }

        final UserCityEntity userCity = new UserCityEntity(foundUser.get().getId(), cityName);
        final UserCityEntity savingResult = usersCitiesRepository.save(userCity);
        if (!savingResult.equals(userCity)) {
            log.error("can't save {} to user with telegram id {}", cityName, telegramId);
            throw new InternalErrorException();
        }
        log.info("successfully added {} ot user {}", cityName, telegramId);
        return modelMapper.map(savingResult, UserCityDto.class);
    }

    @Override
    public UserCityDto deleteUserCity(long telegramId, String cityName) throws UserNotFoundException, CityNotAddedException {
        final Optional<UserEntity> foundUser = usersRepository.findByTelegramId(telegramId);
        if (foundUser.isEmpty()) {
            log.error("can't delete city because user with telegram id {} not found", telegramId);
            throw new UserNotFoundException();
        }

        final Optional<UserCityEntity> testUnique = usersCitiesRepository.findById(
                new UserCityEmbeddedEntity(foundUser.get().getId(), cityName)
        );
        if (testUnique.isEmpty()) {
            log.warn("can't delete city because user with telegram id {} don't have city {}", telegramId, cityName);
            throw new CityNotAddedException();
        }

        final UserCityEntity userCity = new UserCityEntity(foundUser.get().getId(), cityName);
        usersCitiesRepository.delete(userCity);
        log.info("successfully deleted {} ot user {}", cityName, telegramId);
        return modelMapper.map(userCity, UserCityDto.class);
    }

    @Override
    public List<String> getUserCities(long telegramId) throws UserNotFoundException, InternalErrorException {
        final Optional<UserEntity> foundUser = usersRepository.findByTelegramId(telegramId);
        if (foundUser.isEmpty()) {
            log.error("can't get cities because user with telegram id {} not found", telegramId);
            throw new UserNotFoundException();
        }

        final var userCities = usersCitiesRepository.getUserCities(foundUser.get().getId());
        if (userCities.isEmpty()) {
            log.warn("no cities was found for user {}", telegramId);
            throw new InternalErrorException();
        }
        log.info("successfully found cities for user {}", telegramId);
        return userCities.get();
    }
}
