package com.hrthub.newhire.service;

import com.hrthub.newhire.entity.DocumentValidationStatus;

public record DocumentValidationResult(DocumentValidationStatus status, String notes) {
}
