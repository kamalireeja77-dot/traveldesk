package com.traveldesk.connector;

import com.traveldesk.model.BookingRequest;
import com.traveldesk.model.FlightOption;
import java.util.List;

public interface FlightSearchConnector {
    List<FlightOption> search(BookingRequest request);
}