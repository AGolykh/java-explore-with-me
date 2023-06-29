package ru.practicum;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class StatsClient {
    private final String serverUrl;
    private final RestTemplate restTemplate;

    public StatsClient(@Value("${stats-server.url}") String serverUrl) {
        this.serverUrl = serverUrl;
        this.restTemplate = new RestTemplate();
    }

    public void post(EndpointHitDto endpointHitDto) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<EndpointHitDto> requestEntity = new HttpEntity<>(endpointHitDto, headers);
        restTemplate.exchange(serverUrl + "/hit", HttpMethod.POST, requestEntity, EndpointHitDto.class);
    }

    public List<ViewStatsDto> get(String start, String end, List<String> uris, Boolean unique) {
        Map<String, Object> params = new HashMap<>();
        params.put("start", start);
        params.put("end", end);
        params.put("uris", uris);
        params.put("unique", unique);

        ResponseEntity<String> response = restTemplate.getForEntity(
                serverUrl + "/stats?start={start}&end={end}&uris={uris}&unique={unique}",
                String.class, params);

        ObjectMapper objectMapper = new ObjectMapper();

        try {
            return Arrays.asList(objectMapper.readValue(response.getBody(), ViewStatsDto[].class));
        } catch (JsonProcessingException exception) {
            throw new StatsException(String.format("Json processing error: %s", exception.getMessage()));
        }
    }

    public static class StatsException extends RuntimeException {
        public StatsException(String message) {
            super(message);
        }
    }
}
