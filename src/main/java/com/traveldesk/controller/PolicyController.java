package com.traveldesk.controller;

import com.traveldesk.model.BookingRequest;
import com.traveldesk.service.PolicyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/policy")
public class PolicyController {

    private final PolicyService policyService;

    public PolicyController(PolicyService policyService) {
        this.policyService = policyService;
    }

    @PostMapping("/check")
    public ResponseEntity<Map<String, Object>> checkPolicy(
            @RequestBody BookingRequest request) {
        try {
            Map<String, Object> result = policyService.checkPolicy(request);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404)
                    .body(Map.of("error", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(500)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/grades/{clientId}")
    public ResponseEntity<?> getSupportedGrades(@PathVariable String clientId) {
        // useful for the frontend dropdown
        return ResponseEntity.ok(Map.of("clientId", clientId,
                "grades", new String[]{"L1", "L2", "L3", "L4", "L5"}));
    }
}