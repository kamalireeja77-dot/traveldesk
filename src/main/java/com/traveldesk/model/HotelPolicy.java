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
public class HotelPolicy {
    private int maxStarRating;
    private double maxRatePerNight;   // INR
    private List<String> allowedCities; // ["all"] or specific cities
}