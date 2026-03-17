package com.traveldesk.connector;

import java.util.Map;
import java.util.Optional;

public interface HRConnector {
    Optional<Map<String, Object>> getEmployee(String employeeId);
}