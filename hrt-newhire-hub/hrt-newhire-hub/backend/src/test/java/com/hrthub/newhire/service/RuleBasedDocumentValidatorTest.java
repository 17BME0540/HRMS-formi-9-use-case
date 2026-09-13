package com.hrthub.newhire.service;

import com.hrthub.newhire.entity.DocumentValidationStatus;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RuleBasedDocumentValidatorTest {

    private final RuleBasedDocumentValidator validator = new RuleBasedDocumentValidator();

    @Test
    void marksDocumentCompleteWhenAllFieldsPresent() {
        String doc = "Name: Jane Doe\nSSN last 4: 1234\nDate: 2026-09-01";
        DocumentValidationResult result = validator.validate(doc);
        assertThat(result.status()).isEqualTo(DocumentValidationStatus.COMPLETE);
    }

    @Test
    void marksDocumentIncompleteWhenMissingSsn() {
        String doc = "Name: Jane Doe\nDate: 2026-09-01";
        DocumentValidationResult result = validator.validate(doc);
        assertThat(result.status()).isEqualTo(DocumentValidationStatus.INCOMPLETE);
        assertThat(result.notes()).contains("SSN last-4 reference");
    }

    @Test
    void marksDocumentIncompleteWhenMissingDate() {
        String doc = "Name: Jane Doe\nSSN last 4: 1234";
        DocumentValidationResult result = validator.validate(doc);
        assertThat(result.status()).isEqualTo(DocumentValidationStatus.INCOMPLETE);
        assertThat(result.notes()).contains("a date field");
    }

    @Test
    void marksDocumentIncompleteWhenMissingName() {
        String doc = "SSN last 4: 1234\nDate: 2026-09-01";
        DocumentValidationResult result = validator.validate(doc);
        assertThat(result.status()).isEqualTo(DocumentValidationStatus.INCOMPLETE);
        assertThat(result.notes()).contains("full name field");
    }

    @Test
    void marksBlankDocumentIncomplete() {
        DocumentValidationResult result = validator.validate("   ");
        assertThat(result.status()).isEqualTo(DocumentValidationStatus.INCOMPLETE);
    }

    @Test
    void marksNullDocumentIncomplete() {
        DocumentValidationResult result = validator.validate(null);
        assertThat(result.status()).isEqualTo(DocumentValidationStatus.INCOMPLETE);
    }

    @Test
    void acceptsSlashFormattedDate() {
        String doc = "Name: Jane Doe\nSSN last 4: 1234\nDate: 9/1/2026";
        DocumentValidationResult result = validator.validate(doc);
        assertThat(result.status()).isEqualTo(DocumentValidationStatus.COMPLETE);
    }
}
