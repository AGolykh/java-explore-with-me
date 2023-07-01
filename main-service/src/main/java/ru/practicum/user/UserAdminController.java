package ru.practicum.user;

import lombok.RequiredArgsConstructor;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.user.dto.UserDto;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/users")
@Validated
public class UserAdminController {

    private final UserService userService;

    @GetMapping()
    public List<UserDto> getAll(@RequestParam(required = false) List<Long> ids,
                                @RequestParam(defaultValue = "0") Integer from,
                                @RequestParam(defaultValue = "10") Integer size,
                                HttpServletRequest request) {
        return userService.getAll(ids, from, size, request);
    }

    @PostMapping()
    public UserDto save(@RequestBody @Valid UserDto userDto, HttpServletRequest request) {
        return userService.create(userDto, request);
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable Long userId, HttpServletRequest request) {
        userService.deleteById(userId, request);
    }
}
