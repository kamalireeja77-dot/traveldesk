package com.traveldesk.connector;

import com.traveldesk.model.BookingRequest;
import com.traveldesk.model.FlightOption;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import java.util.*;

@Component
@Profile("prod")
public class AmadeusFlightConnector implements FlightSearchConnector {

    @Value("${amadeus.api.url}")
    private String apiUrl;

    @Value("${amadeus.api.key}")
    private String apiKey;

    @Value("${amadeus.api.secret}")
    private String apiSecret;

    private final RestTemplate restTemplate = new RestTemplate();

    private String getAccessToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        String body = "grant_type=client_credentials"
                + "&client_id=" + apiKey
                + "&client_secret=" + apiSecret;
        ResponseEntity<Map> response = restTemplate.postForEntity(
                apiUrl + "/v1/security/oauth2/token",
                new HttpEntity<>(body, headers),
                Map.class
        );
        return (String) Objects.requireNonNull(response.getBody()).get("access_token");
    }

    @Override
    public List<FlightOption> search(BookingRequest request) {
        String token = getAccessToken();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        String url = String.format(
                "%s/v2/shopping/flight-offers?originLocationCode=%s"
                        + "&destinationLocationCode=%s&departureDate=%s&adults=1&travelClass=%s",
                apiUrl,
                request.getOrigin(),
                request.getDestination(),
                request.getDepartureDate(),
                "ECONOMY"
        );

        ResponseEntity<Map> response = restTemplate.exchange(
                url, HttpMethod.GET, new HttpEntity<>(headers), Map.class
        );

        return parseAmadeusResponse(response.getBody());
    }

    @SuppressWarnings("unchecked")
    private List<FlightOption> parseAmadeusResponse(Map<String, Object> body) {
        List<FlightOption> results = new ArrayList<>();
        List<Map<String, Object>> offers =
                (List<Map<String, Object>>) body.get("data");

        if (offers == null) return results;

        for (Map<String, Object> offer : offers) {
            Map<String, Object> price =
                    (Map<String, Object>) offer.get("price");
            double base = Double.parseDouble(
                    (String) price.get("base")
            );
            results.add(FlightOption.builder()
                    .flightId((String) offer.get("id"))
                    .baseFare(base)
                    .cabinClass("economy")
                    .build());
        }
        return results;
    }
}