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
public class PricingResult {
    private String employeeId;
    private String employeeName;
    private String grade;

    private List<FlightOption> compliantFlights;    // within policy
    private List<FlightOption> nonCompliantFlights; // shown greyed out

    private List<HotelOption> compliantHotels;
    private List<HotelOption> nonCompliantHotels;

    // Pre-computed cheapest compliant combination
    private FlightOption recommendedFlight;
    private HotelOption  recommendedHotel;
    private CostBreakdown costBreakdown;

    private String searchId;   // UUID — used by approval agent to lock price
}