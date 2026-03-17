package com.traveldesk.model;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CostBreakdown {
    private double flightBaseFare;
    private double flightAddOns;     // meals + luggage + flexi
    private double flightGst;
    private double flightTotal;

    private double hotelRoomCost;
    private double hotelTax;
    private double hotelTotal;

    private double perDiemTotal;     // perDiem × nights

    private double grandTotal;       // everything combined
    private boolean requiresApproval;
    private double approvalThreshold;

    // Price snapshot timestamp — sent to approval agent
    private long snapshotTimestamp;
}