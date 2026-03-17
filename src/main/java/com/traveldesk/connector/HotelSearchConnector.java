package com.traveldesk.connector;

import com.traveldesk.model.BookingRequest;
import com.traveldesk.model.HotelOption;
import java.util.List;

public interface HotelSearchConnector {
    List<HotelOption> search(BookingRequest request);
}