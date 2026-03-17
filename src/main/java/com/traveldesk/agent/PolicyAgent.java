package com.traveldesk.agent;

import com.traveldesk.config.PolicyConfig.GradePolicy;
import com.traveldesk.connector.HRConnector;
import com.traveldesk.model.*;
import com.traveldesk.repository.PolicyRepository;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Component
public class PolicyAgent {

    private final HRConnector hrConnector;
    private final PolicyRepository policyRepository;

    public PolicyAgent(HRConnector hrConnector, PolicyRepository policyRepository) {
        this.hrConnector = hrConnector;
        this.policyRepository = policyRepository;
    }

    public PolicyContext run(String employeeId, BookingRequest request) {

        // 1. Fetch employee from HR system
        Map<String, Object> employee = hrConnector.getEmployee(employeeId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Employee " + employeeId + " not found in HR system"
                ));

        // 2. Load policy rules for this client + grade
        String clientId = (String) employee.get("clientId");
        String grade    = (String) employee.get("grade");

        GradePolicy rules = policyRepository.findPolicy(clientId, grade)
                .orElseThrow(() -> new IllegalStateException(
                        "No policy found for client=" + clientId + " grade=" + grade
                ));

        // 3. Build PolicyContext
        PolicyContext context = buildContext(employeeId, employee, rules);

        // 4. Validate trip against policy
        validateTrip(context, request);

        return context;
    }

    // ── Build ─────────────────────────────────────────────────────────────────

    @SuppressWarnings("unchecked")
    private PolicyContext buildContext(String employeeId,
                                       Map<String, Object> emp,
                                       GradePolicy rules) {
        FlightPolicy flightPolicy = FlightPolicy.builder()
                .domesticClass(rules.getFlight().getDomesticClass())
                .internationalClass(rules.getFlight().getInternationalClass())
                .maxFareDomestic(rules.getFlight().getMaxFareDomestic())
                .maxFareInternational(rules.getFlight().getMaxFareInternational())
                .allowedAirlines(rules.getFlight().getAllowedAirlines())
                .advanceBookingDays(rules.getFlight().getAdvanceBookingDays())
                .build();

        HotelPolicy hotelPolicy = HotelPolicy.builder()
                .maxStarRating(rules.getHotel().getMaxStarRating())
                .maxRatePerNight(rules.getHotel().getMaxRatePerNight())
                .allowedCities(rules.getHotel().getAllowedCities())
                .build();

        return PolicyContext.builder()
                .employeeId(employeeId)
                .employeeName((String) emp.get("name"))
                .grade((String) emp.get("grade"))
                .department((String) emp.get("department"))
                .costCentre((String) emp.get("costCentre"))
                .flight(flightPolicy)
                .hotel(hotelPolicy)
                .perDiem(rules.getPerDiem())
                .requiresApprovalAbove(rules.getRequiresApprovalAbove())
                .loyaltyNumbers((Map<String, String>) emp.getOrDefault("loyaltyNumbers", Map.of()))
                .gstNumber((String) emp.getOrDefault("gstNumber", ""))
                .build();
    }

    // ── Validate ──────────────────────────────────────────────────────────────

    private void validateTrip(PolicyContext ctx, BookingRequest request) {

        // Check advance booking window
        long daysAhead = ChronoUnit.DAYS.between(LocalDate.now(), request.getDepartureDate());
        if (daysAhead < ctx.getFlight().getAdvanceBookingDays()) {
            ctx.addViolation(String.format(
                    "Booking must be made %d days in advance. Currently only %d days away.",
                    ctx.getFlight().getAdvanceBookingDays(), daysAhead
            ));
        }

        // Flag unusually long trips
        if (request.getNights() > 14) {
            ctx.addViolation("Trip exceeds 14 nights — manager pre-approval required.");
        }

        // Check departure date is not in the past
        if (request.getDepartureDate().isBefore(LocalDate.now())) {
            ctx.addViolation("Departure date cannot be in the past.");
        }
    }
}