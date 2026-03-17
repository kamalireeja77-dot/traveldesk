package com.traveldesk.model;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HotelOption {
    private String hotelId;
    private String hotelName;
    private String city;
    private int starRating;
    private double ratePerNight;
    private int nights;
    private double totalRoomCost;   // ratePerNight × nights
    private double taxAmount;       // 12% GST on hotels
    private double totalHotelCost;  // totalRoomCost + taxAmount
    private boolean withinPolicy;
    private String policyViolationReason;
}