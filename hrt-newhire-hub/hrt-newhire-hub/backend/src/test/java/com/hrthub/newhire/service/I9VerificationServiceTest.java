package com.hrthub.newhire.service;

import com.hrthub.newhire.entity.I9Status;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class I9VerificationServiceTest {

    private final I9VerificationService service = new I9VerificationService();

    @Test
    void verifiesEmployeeWithValidNameAndPastHireDate() {
        I9Status status = service.verify("Jane Doe", LocalDate.now().minusDays(1));
        assertThat(status).isEqualTo(I9Status.VERIFIED);
    }

    @Test
    void verifiesEmployeeHiredToday() {
        I9Status status = service.verify("Jane Doe", LocalDate.now());
        assertThat(status).isEqualTo(I9Status.VERIFIED);
    }

    @Test
    void marksFutureHireDateAsPending() {
        I9Status status = service.verify("Jane Doe", LocalDate.now().plusDays(5));
        assertThat(status).isEqualTo(I9Status.PENDING);
    }

    @Test
    void rejectsBlankName() {
        I9Status status = service.verify("   ", LocalDate.now().minusDays(1));
        assertThat(status).isEqualTo(I9Status.REJECTED);
    }

    @Test
    void rejectsNullName() {
        I9Status status = service.verify(null, LocalDate.now().minusDays(1));
        assertThat(status).isEqualTo(I9Status.REJECTED);
    }

    @Test
    void marksNullHireDateAsPending() {
        I9Status status = service.verify("Jane Doe", null);
        assertThat(status).isEqualTo(I9Status.PENDING);
    }
}
