package com.hrthub.newhire.service;

import com.hrthub.newhire.entity.I9Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

/**
 * Stands in for an integration with an external I-9 Tracker system.
 *
 * In a production system this would call out over HTTP (or a message queue)
 * to the vendor's I-9 API and poll/callback for a result. Here the same
 * contract is kept - given a name and hire date, return an I9Status - so the
 * rest of the application (controller, tests, frontend) doesn't need to
 * change when the simulated call is swapped for a real one.
 */
@Service
public class I9VerificationService {

    private static final Logger log = LoggerFactory.getLogger(I9VerificationService.class);

    public I9Status verify(String employeeName, LocalDate hireDate) {
        log.info("Calling external I-9 Tracker for employee '{}' with hire date {}", employeeName, hireDate);

        if (employeeName == null || employeeName.isBlank()) {
            return I9Status.REJECTED;
        }

        if (hireDate == null || hireDate.isAfter(LocalDate.now())) {
            // A real I-9 Tracker can't verify eligibility for a hire date that
            // hasn't happened yet - the case is left pending until it does.
            return I9Status.PENDING;
        }

        return I9Status.VERIFIED;
    }
}
