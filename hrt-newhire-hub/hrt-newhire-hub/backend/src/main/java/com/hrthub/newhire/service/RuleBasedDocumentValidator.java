package com.hrthub.newhire.service;

import com.hrthub.newhire.entity.DocumentValidationStatus;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Deterministic stand-in for the AI validation step. Checks that the
 * submitted onboarding document text contains the pieces a real I-9 /
 * HRSD intake would require: a full name, a 4-digit SSN reference, and a
 * date. This is what runs by default so the project has zero external
 * dependencies out of the box.
 */
@Component
@ConditionalOnProperty(name = "newhire.ai-validation.enabled", havingValue = "false", matchIfMissing = true)
public class RuleBasedDocumentValidator implements DocumentValidator {

    // Field patterns are anchored to their label (e.g. "SSN last 4: 1234") rather than a
    // bare 4-digit number, so a document's own date year can never be mistaken for an SSN.
    private static final Pattern SSN_LAST_FOUR = Pattern.compile("(?i)ssn[^:\\n]*:\\s*\\d{4}\\b");
    private static final Pattern DATE_PATTERN = Pattern.compile("(?i)date\\s*:\\s*(\\d{4}-\\d{2}-\\d{2}|\\d{1,2}/\\d{1,2}/\\d{2,4})");
    private static final Pattern NAME_FIELD = Pattern.compile("(?i)name\\s*:\\s*\\S+");

    @Override
    public DocumentValidationResult validate(String documentText) {
        if (documentText == null || documentText.isBlank()) {
            return new DocumentValidationResult(
                    DocumentValidationStatus.INCOMPLETE,
                    "Document is empty.");
        }

        List<String> missing = new ArrayList<>();
        if (!NAME_FIELD.matcher(documentText).find()) {
            missing.add("full name field");
        }
        if (!SSN_LAST_FOUR.matcher(documentText).find()) {
            missing.add("SSN last-4 reference");
        }
        if (!DATE_PATTERN.matcher(documentText).find()) {
            missing.add("a date field");
        }

        if (missing.isEmpty()) {
            return new DocumentValidationResult(
                    DocumentValidationStatus.COMPLETE,
                    "All required fields detected.");
        }

        return new DocumentValidationResult(
                DocumentValidationStatus.INCOMPLETE,
                "Missing: " + String.join(", ", missing) + ".");
    }
}
