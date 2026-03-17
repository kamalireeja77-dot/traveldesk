package com.traveldesk.connector;

import com.traveldesk.model.BookingRequest;
import com.traveldesk.model.FlightOption;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.List;

@Component
@Profile("dev")
public class MockFlightConnector implements FlightSearchConnector {

    @Override
    public List<FlightOption> search(BookingRequest request) {
        // Simulates raw API results — before policy filtering
        return List.of(
                FlightOption.builder()
                        .flightId("6E-101")
                        .airline("IndiGo")
                        .airlineCode("6E")
                        .flightNumber("6E 101")
                        .origin(request.getOrigin())
                        .destination(request.getDestination())
                        .departureTime(request.getDepartureDate().atTime(6, 0))
                        .arrivalTime(request.getDepartureDate().atTime(8, 30))
                        .cabinClass("economy")
                        .baseFare(4500)
                        .mealCost(350)
                        .luggageCost(500)
                        .flexiFare(800)
                        .build(),

                FlightOption.builder()
                        .flightId("AI-202")
                        .airline("Air India")
                        .airlineCode("AI")
                        .flightNumber("AI 202")
                        .origin(request.getOrigin())
                        .destination(request.getDestination())
                        .departureTime(request.getDepartureDate().atTime(9, 0))
                        .arrivalTime(request.getDepartureDate().atTime(11, 30))
                        .cabinClass("business")
                        .baseFare(18000)
                        .mealCost(0)       // included in business
                        .luggageCost(0)    // included in business
                        .flexiFare(2000)
                        .build(),

                FlightOption.builder()
                        .flightId("UK-303")
                        .airline("Vistara")
                        .airlineCode("UK")
                        .flightNumber("UK 303")
                        .origin(request.getOrigin())
                        .destination(request.getDestination())
                        .departureTime(request.getDepartureDate().atTime(14, 0))
                        .arrivalTime(request.getDepartureDate().atTime(16, 30))
                        .cabinClass("economy")
                        .baseFare(6200)
                        .mealCost(350)
                        .luggageCost(500)
                        .flexiFare(800)
                        .build()
        );
    }
}