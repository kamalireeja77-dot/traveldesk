package com.traveldesk.service;

import com.traveldesk.agent.PolicyAgent;
import com.traveldesk.model.*;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service
public class PolicyService {

    private final PolicyAgent policyAgent;

    public PolicyService(PolicyAgent policyAgent) {
        this.policyAgent = policyAgent;
    }

    public Map<String, Object> checkPolicy(BookingRequest request) {
        PolicyContext ctx = policyAgent.run(request.getEmployeeId(), request);

        boolean isDomestic = "domestic".equalsIgnoreCase(request.getTripType());

        return Map.of(
                "isCompliant",            ctx.isCompliant(),
                "violations",             ctx.getViolations(),
                "employeeName",           ctx.getEmployeeName(),
                "grade",                  ctx.getGrade(),
                "requiresApprovalAbove",  ctx.getRequiresApprovalAbove(),
                "allowed", Map.of(
                        "flightClass",        isDomestic
                                ? ctx.getFlight().getDomesticClass()
                                : ctx.getFlight().getInternationalClass(),
                        "maxFlightFare",      isDomestic
                                ? ctx.getFlight().getMaxFareDomestic()
                                : ctx.getFlight().getMaxFareInternational(),
                        "advanceBookingDays", ctx.getFlight().getAdvanceBookingDays(),
                        "hotelMaxStars",      ctx.getHotel().getMaxStarRating(),
                        "hotelMaxPerNight",   ctx.getHotel().getMaxRatePerNight(),
                        "perDiem",            ctx.getPerDiem()
                )
        );
    }
}