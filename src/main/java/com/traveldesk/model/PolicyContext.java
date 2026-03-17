package com.traveldesk.model;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PolicyContext {
    private String employeeId;
    private String employeeName;
    private String grade;
    private String department;
    private String costCentre;
    private FlightPolicy flight;
    private HotelPolicy hotel;
    private double perDiem;
    private double requiresApprovalAbove;
    private Map<String, String> loyaltyNumbers;  // {"indigo": "6E12345"}
    private String gstNumber;

    @Builder.Default
    private List<String> violations = new ArrayList<>();

    @Builder.Default
    private boolean compliant = true;

    public void addViolation(String message) {
        this.violations.add(message);
        this.compliant = false;
    }
}