package ru.practicum.user;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
public class UserDto {

    private Long id;

    @NotBlank
    @Email
    private String name;

    @NotBlank
    @Size(max = 255)
    @Email
    private String email;
}
