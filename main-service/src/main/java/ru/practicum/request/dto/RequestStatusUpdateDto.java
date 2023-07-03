package ru.practicum.request.dto;

import lombok.*;
import ru.practicum.request.Status;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestStatusUpdateDto {

    private List<Long> requestIds;
    private Status status;
}
