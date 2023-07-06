package ru.practicum.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.request.Status;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestsStatusUpdateDto {
    private List<Long> requestIds;
    private Status status;
}
