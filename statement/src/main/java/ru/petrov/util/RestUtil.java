package ru.petrov.util;

import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class RestUtil {
    private final RestTemplate rest;

    public <T> ResponseEntity<T> exchangeDtoToEntity(String url,
                                                     Object dto,
                                                     ParameterizedTypeReference<T> responseType) throws RestClientException {
        return
                rest.exchange(url,
                        HttpMethod.POST,
                        new HttpEntity<>(dto, getHttpHeaders()),
                        responseType);
    }

    private static HttpHeaders getHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}