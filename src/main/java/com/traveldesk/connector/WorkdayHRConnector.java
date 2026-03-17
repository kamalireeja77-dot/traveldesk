package com.traveldesk.connector;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import java.util.*;

@Component
@Profile("prod")  // only active in prod profile
public class WorkdayHRConnector implements HRConnector {

    @Value("${hr.api.url}")
    private String apiUrl;

    @Value("${hr.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public Optional<Map<String, Object>> getEmployee(String employeeId) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + apiKey);
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    apiUrl + "/employees/" + employeeId,
                    HttpMethod.GET,
                    entity,
                    Map.class
            );
            return Optional.ofNullable(response.getBody());
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}