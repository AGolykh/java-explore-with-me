package ru.practicum.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.user.dto.UserDto;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public List<UserDto> getAll(List<Long> ids, Integer from, Integer size) {
        List<UserDto> result = getUsersByParams(ids, getPage(from, size))
                .stream()
                .map(userMapper::toUserDto)
                .collect(Collectors.toList());

        log.info("Found {} user(s).", result.size());
        return result;
    }

    @Transactional
    public UserDto create(UserDto userDto) {
        UserDto result = Optional.of(userRepository.save(userMapper.toUser(userDto)))
                .map(userMapper::toUserDto)
                .orElseThrow();

        log.info("User {} {} created.", result.getId(), result.getName());
        return result;
    }

    @Transactional
    public void deleteById(Long userId) {
        User result = getUserById(userId);
        userRepository.deleteById(result.getId());

        log.info("User {} removed.", result.getName());
    }

    private List<User> getUsersByParams(List<Long> ids, Pageable pageable) {
        return ids == null ?
                userRepository.findAll(pageable).toList() :
                userRepository.findAllByIdIn(ids, pageable);
    }

    public User getUserById(Long userId) {
        User result = userRepository
                .findById(userId)
                .orElseThrow(() -> new NullPointerException(String.format("User %d is not found.", userId)));

        log.info("User {} is found.", result.getId());
        return result;
    }

    private PageRequest getPage(Integer from, Integer size) {
        if (size <= 0 || from < 0) {
            throw new IllegalArgumentException("Page size must not be less than one.");
        }
        return PageRequest.of(from / size, size, Sort.by("id").ascending());
    }
}
