package com.traveldesk.model;

import lombok.Data;
import java.time.LocalDate;

@Data
public class BookingRequest {
    private String    employeeId;
    private LocalDate departureDate;
    private String    origin;          // ← add this
    private String    destination;
    private int       nights;
    private String    tripType;        // "domestic" | "international"
}