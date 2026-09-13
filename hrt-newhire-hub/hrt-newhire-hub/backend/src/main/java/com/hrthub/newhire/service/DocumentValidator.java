package com.hrthub.newhire.service;

/**
 * Simulates the "HRSD content management" style document intake step: a
 * submitted onboarding document is checked for completeness before it is
 * stored and linked to the employee record.
 *
 * Two implementations exist so the AI-assisted path can be swapped in without
 * touching any calling code:
 *  - {@link RuleBasedDocumentValidator} (default): deterministic, offline,
 *    no external dependency - what runs out of the box.
 *  - {@link AiDocumentValidator}: delegates the same check to an LLM, and is
 *    only enabled when newhire.ai-validation.enabled=true and an API key is
 *    configured.
 */
public interface DocumentValidator {
    DocumentValidationResult validate(String documentText);
}
