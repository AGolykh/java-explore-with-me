package ru.practicum.request;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
public class RequestUpdateDto {

    private List<RequestsDto> confirmedRequests;
    private List<RequestsDto> rejectedRequests;
}
