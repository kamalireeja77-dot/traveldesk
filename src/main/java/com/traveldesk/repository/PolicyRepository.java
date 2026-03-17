package com.traveldesk.repository;

import com.traveldesk.config.PolicyConfig;
import com.traveldesk.config.PolicyConfig.GradePolicy;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public class PolicyRepository {

    private final PolicyConfig policyConfig;

    public PolicyRepository(PolicyConfig policyConfig) {
        this.policyConfig = policyConfig;
    }

    public Optional<GradePolicy> findPolicy(String clientId, String grade) {
        return Optional.ofNullable(policyConfig.getPolicyMap())
                .map(m -> m.get(clientId))
                .map(m -> m.get(grade));
    }
}