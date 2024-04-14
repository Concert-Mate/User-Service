package ru.nsu.concertsmate.users_service.services.impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.nsu.concertsmate.users_service.model.dto.UserTracksListDto;
import ru.nsu.concertsmate.users_service.model.entities.User;
import ru.nsu.concertsmate.users_service.model.entities.UserTracksList;
import ru.nsu.concertsmate.users_service.model.entities.UserTracksListEmbedded;
import ru.nsu.concertsmate.users_service.repositories.TracksListsRepository;
import ru.nsu.concertsmate.users_service.repositories.UsersRepository;
import ru.nsu.concertsmate.users_service.services.TracksListsService;
import ru.nsu.concertsmate.users_service.services.exceptions.TracksListAlreadyAdded;
import ru.nsu.concertsmate.users_service.services.exceptions.TracksListNotAdded;
import ru.nsu.concertsmate.users_service.services.exceptions.UserNotFound;

import java.util.List;
import java.util.Optional;

@Service
public class TracksListsServiceImpl implements TracksListsService {
    private final TracksListsRepository tracksListsRepository;
    private final ModelMapper modelMapper;
    private final UsersRepository usersRepository;

    @Autowired
    public TracksListsServiceImpl(TracksListsRepository tracksListsRepository, ModelMapper modelMapper, UsersRepository usersRepository1) {
        this.tracksListsRepository = tracksListsRepository;
        this.modelMapper = modelMapper;
        this.usersRepository = usersRepository1;
    }

    @Override
    public UserTracksListDto saveUserTracksList(long telegramId, String tracksListUrl) throws UserNotFound, TracksListAlreadyAdded {
        Optional<User> user = usersRepository.findByTelegramId(telegramId);
        if (user.isEmpty()) {
            throw new UserNotFound();
        }
        Optional<UserTracksList> testUnique = tracksListsRepository.findById(new UserTracksListEmbedded(user.get().getId(), tracksListUrl));
        if (testUnique.isPresent()) {
            throw new TracksListAlreadyAdded();
        }
        UserTracksList userTracksList = new UserTracksList(user.get().getId(), tracksListUrl);
        UserTracksList result = tracksListsRepository.save(userTracksList);
        if (!result.equals(userTracksList)) {
            throw new RuntimeException("can't save user tracks-list");
        }

        return modelMapper.map(result, UserTracksListDto.class);
    }

    @Override
    public UserTracksListDto deleteUserTracksList(long telegramId, String cityName) throws UserNotFound, TracksListNotAdded {
        Optional<User> user = usersRepository.findByTelegramId(telegramId);
        if (user.isEmpty()) {
            throw new UserNotFound();
        }
        Optional<UserTracksList> testUnique = tracksListsRepository.findById(new UserTracksListEmbedded(user.get().getId(), cityName));
        if (testUnique.isEmpty()) {
            throw new TracksListNotAdded();
        }
        UserTracksList userTracksList = new UserTracksList(user.get().getId(), cityName);
        tracksListsRepository.delete(userTracksList);

        return null;
    }

    @Override
    public List<String> getUserTracksLists(long telegramId) throws UserNotFound {
        Optional<User> user = usersRepository.findByTelegramId(telegramId);
        if (user.isEmpty()) {
            throw new UserNotFound();
        }
        return tracksListsRepository.getUserTracksLists(user.get().getId()).get();
    }
}
