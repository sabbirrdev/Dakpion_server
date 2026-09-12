package com.company.dakpion.config;

import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component("auditorProvider")
public class AuditorAwareImpl implements AuditorAware<Long> {

    private final CurrentUserContext currentUserContext;

    public AuditorAwareImpl(CurrentUserContext currentUserContext) {
        this.currentUserContext = currentUserContext;
    }

    @Override
    public Optional<Long> getCurrentAuditor() {
        try {
            return Optional.ofNullable(currentUserContext.getUserId());
        } catch (Exception e) {
            return Optional.empty(); // No authenticated user found
        }
    }
}
