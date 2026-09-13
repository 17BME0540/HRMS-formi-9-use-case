package com.hrthub.newhire.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class NewHireRequest {

    @NotBlank(message = "name is required")
    private String name;

    @NotNull(message = "hireDate is required")
    private LocalDate hireDate;

    @NotBlank(message = "documentText is required")
    private String documentText;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getHireDate() {
        return hireDate;
    }

    public void setHireDate(LocalDate hireDate) {
        this.hireDate = hireDate;
    }

    public String getDocumentText() {
        return documentText;
    }

    public void setDocumentText(String documentText) {
        this.documentText = documentText;
    }
}
