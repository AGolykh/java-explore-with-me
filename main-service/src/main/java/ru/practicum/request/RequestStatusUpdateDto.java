package ru.practicum.request;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
public class RequestStatusUpdateDto {

    private List<Long> requestIds;
    private Status status;
}
