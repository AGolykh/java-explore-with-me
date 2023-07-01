package ru.practicum.request.dto;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
public class RequestUpdateDto {

    private List<RequestDto> confirmedRequests;
    private List<RequestDto> rejectedRequests;
}
