package com.hrthub.newhire.service;

import com.hrthub.newhire.dto.NewHireRequest;
import com.hrthub.newhire.entity.DocumentValidationStatus;
import com.hrthub.newhire.entity.Employee;
import com.hrthub.newhire.entity.I9Status;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Exercises the full onboarding path end-to-end against the real Spring
 * context and in-memory H2 database: request -> I-9 verification ->
 * document validation -> persistence -> retrieval. This is the scenario
 * that matters most from a QA/end-to-end perspective, complementing the
 * narrower unit tests for each service.
 */
@SpringBootTest
class NewHireOnboardingServiceIntegrationTest {

    @Autowired
    private NewHireOnboardingService onboardingService;

    @Test
    void onboardingACompleteHireEndToEndPersistsVerifiedAndComplete() {
        NewHireRequest request = new NewHireRequest();
        request.setName("Jane Doe");
        request.setHireDate(LocalDate.now().minusDays(2));
        request.setDocumentText("Name: Jane Doe\nSSN last 4: 5678\nDate: 2026-09-01");

        Employee saved = onboardingService.onboard(request);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getI9Status()).isEqualTo(I9Status.VERIFIED);
        assertThat(saved.getDocumentValidationStatus()).isEqualTo(DocumentValidationStatus.COMPLETE);

        Employee reloaded = onboardingService.findById(saved.getId());
        assertThat(reloaded.getName()).isEqualTo("Jane Doe");
    }

    @Test
    void onboardingWithIncompleteDocumentIsFlaggedButStillPersisted() {
        NewHireRequest request = new NewHireRequest();
        request.setName("John Smith");
        request.setHireDate(LocalDate.now().minusDays(1));
        request.setDocumentText("Name: John Smith");

        Employee saved = onboardingService.onboard(request);

        assertThat(saved.getI9Status()).isEqualTo(I9Status.VERIFIED);
        assertThat(saved.getDocumentValidationStatus()).isEqualTo(DocumentValidationStatus.INCOMPLETE);
        assertThat(saved.getDocumentValidationNotes()).contains("SSN last-4 reference");
    }

    @Test
    void onboardingWithFutureHireDateIsPending() {
        NewHireRequest request = new NewHireRequest();
        request.setName("Future Hire");
        request.setHireDate(LocalDate.now().plusDays(10));
        request.setDocumentText("Name: Future Hire\nSSN last 4: 1111\nDate: 2026-09-01");

        Employee saved = onboardingService.onboard(request);

        assertThat(saved.getI9Status()).isEqualTo(I9Status.PENDING);
    }
}
