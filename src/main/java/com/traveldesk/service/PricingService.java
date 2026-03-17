package com.traveldesk.service;

import com.traveldesk.agent.PolicyAgent;
import com.traveldesk.agent.PricingAgent;
import com.traveldesk.model.*;
import org.springframework.stereotype.Service;

@Service
public class PricingService {

    private final PolicyAgent  policyAgent;
    private final PricingAgent pricingAgent;

    public PricingService(PolicyAgent policyAgent, PricingAgent pricingAgent) {
        this.policyAgent  = policyAgent;
        this.pricingAgent = pricingAgent;
    }

    public PricingResult search(BookingRequest request) {
        // 1. Run Policy Agent first
        PolicyContext policy = policyAgent.run(
                request.getEmployeeId(), request
        );

        // 2. Run Pricing Agent with policy context
        return pricingAgent.run(policy, request);
    }
}