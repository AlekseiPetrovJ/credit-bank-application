package ru.petrov.util;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
public class RestUtilTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private RestUtil restUtil;

    @Test
    public void testExchangeDtoToEntity() {
        String url = "http://example.com";
        Object dto = new Object(); // Replace with your actual DTO
        ParameterizedTypeReference<String> responseType = new ParameterizedTypeReference<String>() {};
        ResponseEntity<String> expectedResponse = ResponseEntity.ok("response");

        when(restTemplate.exchange(
                eq(url),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(responseType)
        )).thenReturn(expectedResponse);

        ResponseEntity<String> actualResponse = restUtil.exchangeDtoToEntity(url, dto, responseType);

        assertNotNull(actualResponse);
        assertEquals(expectedResponse, actualResponse);
        verify(restTemplate).exchange(eq(url), eq(HttpMethod.POST), any(HttpEntity.class), eq(responseType));
    }
}