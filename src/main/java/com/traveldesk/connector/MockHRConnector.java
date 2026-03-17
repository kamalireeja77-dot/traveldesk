package com.traveldesk.connector;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import java.util.*;

@Component
@Profile("dev")  // only active in dev profile
public class MockHRConnector implements HRConnector {

    private static final Map<String, Map<String, Object>> MOCK_DATA = new HashMap<>();

    static {
        MOCK_DATA.put("EMP001", Map.of(
                "name",           "Sharma",
                "grade",          "L2",
                "department",     "Engineering",
                "costCentre",     "CC-ENG-01",
                "clientId",       "acme_corp",
                "gstNumber",      "27AAPFU0939F1ZV",
                "loyaltyNumbers", Map.of("indigo", "6E12345", "taj", "TAJ98765")
        ));
        MOCK_DATA.put("EMP002", Map.of(
                "name",           "Rishi Kapoor",
                "grade",          "L3",
                "department",     "Sales",
                "costCentre",     "CC-SALES-02",
                "clientId",       "acme_corp",
                "gstNumber",      "27AAPFU0939F1ZV",
                "loyaltyNumbers", Map.of("airIndia", "AI54321")
        ));
    }

    @Override
    public Optional<Map<String, Object>> getEmployee(String employeeId) {
        return Optional.ofNullable(MOCK_DATA.get(employeeId));
    }
}