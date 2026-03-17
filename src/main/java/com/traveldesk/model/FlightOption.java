package com.traveldesk.model;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlightOption {
    private String flightId;
    private String airline;
    private String airlineCode;
    private String flightNumber;
    private String origin;
    private String destination;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private String cabinClass;
    private double baseFare;
    private double mealCost;
    private double luggageCost;
    private double flexiFare;       // cancellation/change fee
    private double gstAmount;
    private double totalFare;       // baseFare + all add-ons + GST
    private boolean withinPolicy;
    private String policyViolationReason;
}