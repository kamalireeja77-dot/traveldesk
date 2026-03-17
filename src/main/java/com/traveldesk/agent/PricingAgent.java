package com.traveldesk.agent;

import com.traveldesk.connector.FlightSearchConnector;
import com.traveldesk.connector.HotelSearchConnector;
import com.traveldesk.model.*;
import org.springframework.stereotype.Component;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class PricingAgent {

    private static final double FLIGHT_GST_RATE = 0.05;  // 5% on flights
    private static final double HOTEL_GST_RATE  = 0.12;  // 12% on hotels

    private final FlightSearchConnector flightConnector;
    private final HotelSearchConnector  hotelConnector;

    public PricingAgent(FlightSearchConnector flightConnector,
                        HotelSearchConnector hotelConnector) {
        this.flightConnector = flightConnector;
        this.hotelConnector  = hotelConnector;
    }

    public PricingResult run(PolicyContext policy, BookingRequest request) {

        // 1. Search raw options from APIs
        List<FlightOption> rawFlights = flightConnector.search(request);
        List<HotelOption>  rawHotels  = hotelConnector.search(request);

        // 2. Enrich with add-ons and GST
        List<FlightOption> enrichedFlights = rawFlights.stream()
                .map(this::enrichFlight)
                .collect(Collectors.toList());

        List<HotelOption> enrichedHotels = rawHotels.stream()
                .map(h -> enrichHotel(h, request.getNights()))
                .collect(Collectors.toList());

        // 3. Apply policy filter
        String allowedCabin = "domestic".equalsIgnoreCase(request.getTripType())
                ? policy.getFlight().getDomesticClass()
                : policy.getFlight().getInternationalClass();

        double maxFlightFare = "domestic".equalsIgnoreCase(request.getTripType())
                ? policy.getFlight().getMaxFareDomestic()
                : policy.getFlight().getMaxFareInternational();

        enrichedFlights = enrichedFlights.stream()
                .map(f -> applyFlightPolicy(f, allowedCabin, maxFlightFare,
                        policy.getFlight().getAllowedAirlines()))
                .collect(Collectors.toList());

        enrichedHotels = enrichedHotels.stream()
                .map(h -> applyHotelPolicy(h,
                        policy.getHotel().getMaxStarRating(),
                        policy.getHotel().getMaxRatePerNight()))
                .collect(Collectors.toList());

        // 4. Split into compliant vs non-compliant
        List<FlightOption> compliantFlights = enrichedFlights.stream()
                .filter(FlightOption::isWithinPolicy)
                .sorted(Comparator.comparingDouble(FlightOption::getTotalFare))
                .collect(Collectors.toList());

        List<FlightOption> nonCompliantFlights = enrichedFlights.stream()
                .filter(f -> !f.isWithinPolicy())
                .collect(Collectors.toList());

        List<HotelOption> compliantHotels = enrichedHotels.stream()
                .filter(HotelOption::isWithinPolicy)
                .sorted(Comparator.comparingDouble(HotelOption::getTotalHotelCost))
                .collect(Collectors.toList());

        List<HotelOption> nonCompliantHotels = enrichedHotels.stream()
                .filter(h -> !h.isWithinPolicy())
                .collect(Collectors.toList());

        // 5. Pick cheapest compliant recommendation
        FlightOption recommendedFlight = compliantFlights.isEmpty()
                ? null : compliantFlights.get(0);
        HotelOption recommendedHotel = compliantHotels.isEmpty()
                ? null : compliantHotels.get(0);

        // 6. Build cost breakdown
        CostBreakdown breakdown = buildBreakdown(
                recommendedFlight, recommendedHotel,
                policy.getPerDiem(), request.getNights(),
                policy.getRequiresApprovalAbove()
        );

        return PricingResult.builder()
                .employeeId(policy.getEmployeeId())
                .employeeName(policy.getEmployeeName())
                .grade(policy.getGrade())
                .compliantFlights(compliantFlights)
                .nonCompliantFlights(nonCompliantFlights)
                .compliantHotels(compliantHotels)
                .nonCompliantHotels(nonCompliantHotels)
                .recommendedFlight(recommendedFlight)
                .recommendedHotel(recommendedHotel)
                .costBreakdown(breakdown)
                .searchId(UUID.randomUUID().toString())
                .build();
    }

    // ── Enrichment ────────────────────────────────────────────────────────────

    private FlightOption enrichFlight(FlightOption f) {
        double addOns  = f.getMealCost() + f.getLuggageCost() + f.getFlexiFare();
        double gst     = (f.getBaseFare() + addOns) * FLIGHT_GST_RATE;
        double total   = f.getBaseFare() + addOns + gst;
        f.setGstAmount(Math.round(gst * 100.0) / 100.0);
        f.setTotalFare(Math.round(total * 100.0) / 100.0);
        return f;
    }

    private HotelOption enrichHotel(HotelOption h, int nights) {
        double roomTotal = h.getRatePerNight() * nights;
        double tax       = roomTotal * HOTEL_GST_RATE;
        double total     = roomTotal + tax;
        h.setNights(nights);
        h.setTotalRoomCost(Math.round(roomTotal * 100.0) / 100.0);
        h.setTaxAmount(Math.round(tax * 100.0) / 100.0);
        h.setTotalHotelCost(Math.round(total * 100.0) / 100.0);
        return h;
    }

    // ── Policy Filters ────────────────────────────────────────────────────────

    private FlightOption applyFlightPolicy(FlightOption f,
                                           String allowedCabin,
                                           double maxFare,
                                           List<String> allowedAirlines) {
        if (!f.getCabinClass().equalsIgnoreCase(allowedCabin)) {
            f.setWithinPolicy(false);
            f.setPolicyViolationReason(
                    "Cabin class '" + f.getCabinClass()
                            + "' not allowed. Permitted: " + allowedCabin
            );
            return f;
        }
        if (f.getTotalFare() > maxFare) {
            f.setWithinPolicy(false);
            f.setPolicyViolationReason(
                    "Total fare ₹" + f.getTotalFare()
                            + " exceeds policy limit ₹" + maxFare
            );
            return f;
        }
        if (!allowedAirlines.contains("all")
                && !allowedAirlines.contains(f.getAirlineCode())) {
            f.setWithinPolicy(false);
            f.setPolicyViolationReason(
                    "Airline " + f.getAirline() + " not in allowed list"
            );
            return f;
        }
        f.setWithinPolicy(true);
        return f;
    }

    private HotelOption applyHotelPolicy(HotelOption h,
                                         int maxStars,
                                         double maxPerNight) {
        if (h.getStarRating() > maxStars) {
            h.setWithinPolicy(false);
            h.setPolicyViolationReason(
                    h.getStarRating() + "-star hotel exceeds policy limit of "
                            + maxStars + " stars"
            );
            return h;
        }
        if (h.getRatePerNight() > maxPerNight) {
            h.setWithinPolicy(false);
            h.setPolicyViolationReason(
                    "Rate ₹" + h.getRatePerNight()
                            + "/night exceeds policy limit ₹" + maxPerNight
            );
            return h;
        }
        h.setWithinPolicy(true);
        return h;
    }

    // ── Cost Breakdown ────────────────────────────────────────────────────────

    private CostBreakdown buildBreakdown(FlightOption flight,
                                         HotelOption hotel,
                                         double perDiem,
                                         int nights,
                                         double approvalThreshold) {
        double flightTotal  = flight != null ? flight.getTotalFare()     : 0;
        double flightBase   = flight != null ? flight.getBaseFare()      : 0;
        double flightAddOns = flight != null
                ? (flight.getMealCost() + flight.getLuggageCost()
                + flight.getFlexiFare()) : 0;
        double flightGst    = flight != null ? flight.getGstAmount()     : 0;

        double hotelTotal   = hotel  != null ? hotel.getTotalHotelCost() : 0;
        double hotelRoom    = hotel  != null ? hotel.getTotalRoomCost()  : 0;
        double hotelTax     = hotel  != null ? hotel.getTaxAmount()      : 0;

        double perDiemTotal = perDiem * nights;
        double grandTotal   = flightTotal + hotelTotal + perDiemTotal;

        return CostBreakdown.builder()
                .flightBaseFare(flightBase)
                .flightAddOns(flightAddOns)
                .flightGst(flightGst)
                .flightTotal(flightTotal)
                .hotelRoomCost(hotelRoom)
                .hotelTax(hotelTax)
                .hotelTotal(hotelTotal)
                .perDiemTotal(perDiemTotal)
                .grandTotal(Math.round(grandTotal * 100.0) / 100.0)
                .requiresApproval(grandTotal > approvalThreshold)
                .approvalThreshold(approvalThreshold)
                .snapshotTimestamp(System.currentTimeMillis())
                .build();
    }
}