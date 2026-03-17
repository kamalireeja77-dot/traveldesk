package com.traveldesk.connector;

import com.traveldesk.model.BookingRequest;
import com.traveldesk.model.HotelOption;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@Profile("dev")
public class MockHotelConnector implements HotelSearchConnector {

    @Override
    public List<HotelOption> search(BookingRequest request) {
        return List.of(
                HotelOption.builder()
                        .hotelId("H001")
                        .hotelName("Ibis Mumbai")
                        .city(request.getDestination())
                        .starRating(3)
                        .ratePerNight(2800)
                        .nights(request.getNights())
                        .build(),

                HotelOption.builder()
                        .hotelId("H002")
                        .hotelName("Marriott Mumbai")
                        .city(request.getDestination())
                        .starRating(5)
                        .ratePerNight(11000)
                        .nights(request.getNights())
                        .build(),

                HotelOption.builder()
                        .hotelId("H003")
                        .hotelName("Novotel Mumbai")
                        .city(request.getDestination())
                        .starRating(4)
                        .ratePerNight(5500)
                        .nights(request.getNights())
                        .build()
        );
    }
}