package ru.practicum.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EndpointHitDto {

    private Long id;
    private String app;
    private String uri;
    private String ip;
    private String timestamp;

    public EndpointHitDto(Long id, String app, String uri, String ip) {
        this.id = id;
        this.app = app;
        this.uri = uri;
        this.ip = ip;
    }
}