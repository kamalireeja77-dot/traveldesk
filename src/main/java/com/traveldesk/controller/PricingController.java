package com.traveldesk.controller;

import com.traveldesk.model.BookingRequest;
import com.traveldesk.model.PricingResult;
import com.traveldesk.service.PricingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/pricing")
public class PricingController {

    private final PricingService pricingService;

    public PricingController(PricingService pricingService) {
        this.pricingService = pricingService;
    }

    @PostMapping("/search")
    public ResponseEntity<?> search(@RequestBody BookingRequest request) {
        try {
            PricingResult result = pricingService.search(request);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}