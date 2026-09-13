package com.hrthub.newhire.dto;

import com.hrthub.newhire.entity.DocumentValidationStatus;
import com.hrthub.newhire.entity.Employee;
import com.hrthub.newhire.entity.I9Status;

import java.time.LocalDate;

public class EmployeeResponse {

    private final Long id;
    private final String name;
    private final LocalDate hireDate;
    private final I9Status i9Status;
    private final DocumentValidationStatus documentValidationStatus;
    private final String documentValidationNotes;

    public EmployeeResponse(Employee employee) {
        this.id = employee.getId();
        this.name = employee.getName();
        this.hireDate = employee.getHireDate();
        this.i9Status = employee.getI9Status();
        this.documentValidationStatus = employee.getDocumentValidationStatus();
        this.documentValidationNotes = employee.getDocumentValidationNotes();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public LocalDate getHireDate() {
        return hireDate;
    }

    public I9Status getI9Status() {
        return i9Status;
    }

    public DocumentValidationStatus getDocumentValidationStatus() {
        return documentValidationStatus;
    }

    public String getDocumentValidationNotes() {
        return documentValidationNotes;
    }
}
