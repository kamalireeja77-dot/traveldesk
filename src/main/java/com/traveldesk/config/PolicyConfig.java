package com.traveldesk.config;

import com.traveldesk.model.FlightPolicy;
import com.traveldesk.model.HotelPolicy;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Map;

@Data
@Configuration
@ConfigurationProperties(prefix = "")
public class PolicyConfig {
    private Map<String, Map<String, GradePolicy>> policyMap;


    @Data
    public static class GradePolicy {
        private FlightPolicyConfig flight;
        private HotelPolicyConfig hotel;
        private double perDiem;
        private double requiresApprovalAbove;
    }

    @Data
    public static class FlightPolicyConfig {
        private String domesticClass;
        private String internationalClass;
        private double maxFareDomestic;
        private double maxFareInternational;
        private List<String> allowedAirlines;
        private int advanceBookingDays;
    }

    @Data
    public static class HotelPolicyConfig {
        private int maxStarRating;
        private double maxRatePerNight;
        private List<String> allowedCities;
    }
}