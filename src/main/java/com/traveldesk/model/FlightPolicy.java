package com.traveldesk.model;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlightPolicy {
    private String domesticClass;         // "economy" | "business" | "first"
    private String internationalClass;
    private double maxFareDomestic;       // INR
    private double maxFareInternational;
    private List<String> allowedAirlines; // ["all"] or specific IATA codes
    private int advanceBookingDays;       // must book N days in advance
}