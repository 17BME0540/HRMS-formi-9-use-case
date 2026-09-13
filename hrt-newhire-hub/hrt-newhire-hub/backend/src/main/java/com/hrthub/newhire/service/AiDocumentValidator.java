package com.hrthub.newhire.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hrthub.newhire.entity.DocumentValidationStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

/**
 * AI-assisted equivalent of {@link RuleBasedDocumentValidator}. Sends the
 * submitted document text to an LLM and asks it to judge completeness.
 *
 * This class is the deliberate embodiment of "use AI tools responsibly" and
 * "validate AI-generated output before it is used": the model's answer is
 * never trusted at face value. It must return one of a small fixed set of
 * statuses in a strict JSON shape; anything that fails to parse, or that the
 * model itself is unsure about, is mapped to NEEDS_HUMAN_REVIEW rather than
 * silently passed through as COMPLETE. A human always makes the final call
 * on an incomplete or uncertain document - the AI only triages.
 *
 * Disabled by default. Enable with:
 *   newhire.ai-validation.enabled=true
 *   ANTHROPIC_API_KEY=sk-ant-...  (environment variable)
 */
@Component
@ConditionalOnProperty(name = "newhire.ai-validation.enabled", havingValue = "true")
public class AiDocumentValidator implements DocumentValidator {

    private static final Logger log = LoggerFactory.getLogger(AiDocumentValidator.class);

    private final RestClient restClient;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String apiKey;
    private final String model;

    public AiDocumentValidator(
            @Value("${newhire.ai-validation.api-key}") String apiKey,
            @Value("${newhire.ai-validation.model}") String model) {
        this.apiKey = apiKey;
        this.model = model;
        this.restClient = RestClient.builder()
                .baseUrl("https://api.anthropic.com/v1/messages")
                .build();
    }

    @Override
    public DocumentValidationResult validate(String documentText) {
        if (apiKey == null || apiKey.isBlank()) {
            log.warn("AI validation is enabled but no API key is configured; routing to human review.");
            return new DocumentValidationResult(
                    DocumentValidationStatus.NEEDS_HUMAN_REVIEW,
                    "AI validation enabled but no API key configured.");
        }

        String prompt = """
                You are checking a new-hire onboarding document for completeness.
                Respond with ONLY a JSON object, no other text, of the exact shape:
                {"status": "COMPLETE" | "INCOMPLETE", "notes": "<one short sentence>"}

                A document is COMPLETE only if it clearly contains a full name,
                a 4-digit SSN reference, and a date. Otherwise it is INCOMPLETE.

                Document:
                %s
                """.formatted(documentText);

        try {
            Map<String, Object> requestBody = Map.of(
                    "model", model,
                    "max_tokens", 200,
                    "messages", new Object[]{
                            Map.of("role", "user", "content", prompt)
                    });

            String rawResponse = restClient.post()
                    .header("x-api-key", apiKey)
                    .header("anthropic-version", "2023-06-01")
                    .header("content-type", "application/json")
                    .body(requestBody)
                    .retrieve()
                    .body(String.class);

            return parseModelResponse(rawResponse);
        } catch (Exception e) {
            log.error("AI document validation call failed; routing to human review.", e);
            return new DocumentValidationResult(
                    DocumentValidationStatus.NEEDS_HUMAN_REVIEW,
                    "AI validation call failed: " + e.getMessage());
        }
    }

    private DocumentValidationResult parseModelResponse(String rawResponse) {
        try {
            JsonNode root = objectMapper.readTree(rawResponse);
            String modelText = root.path("content").get(0).path("text").asText();
            JsonNode parsed = objectMapper.readTree(modelText);

            String statusText = parsed.path("status").asText("");
            String notes = parsed.path("notes").asText("No notes returned.");

            return switch (statusText) {
                case "COMPLETE" -> new DocumentValidationResult(DocumentValidationStatus.COMPLETE, notes);
                case "INCOMPLETE" -> new DocumentValidationResult(DocumentValidationStatus.INCOMPLETE, notes);
                default -> new DocumentValidationResult(
                        DocumentValidationStatus.NEEDS_HUMAN_REVIEW,
                        "Model returned an unrecognized status ('" + statusText + "'); flagged for human review.");
            };
        } catch (Exception e) {
            log.warn("Could not parse AI validator response as expected JSON; routing to human review.", e);
            return new DocumentValidationResult(
                    DocumentValidationStatus.NEEDS_HUMAN_REVIEW,
                    "AI response was not valid JSON and could not be trusted automatically.");
        }
    }
}
