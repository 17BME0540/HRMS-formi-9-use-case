package com.hrthub.newhire.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "employees")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private LocalDate hireDate;

    @Lob
    private String documentText;

    @Enumerated(EnumType.STRING)
    private I9Status i9Status;

    @Enumerated(EnumType.STRING)
    private DocumentValidationStatus documentValidationStatus;

    @Column(length = 1000)
    private String documentValidationNotes;

    protected Employee() {
        // required by JPA
    }

    public Employee(String name, LocalDate hireDate, String documentText) {
        this.name = name;
        this.hireDate = hireDate;
        this.documentText = documentText;
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

    public String getDocumentText() {
        return documentText;
    }

    public I9Status getI9Status() {
        return i9Status;
    }

    public void setI9Status(I9Status i9Status) {
        this.i9Status = i9Status;
    }

    public DocumentValidationStatus getDocumentValidationStatus() {
        return documentValidationStatus;
    }

    public void setDocumentValidationStatus(DocumentValidationStatus documentValidationStatus) {
        this.documentValidationStatus = documentValidationStatus;
    }

    public String getDocumentValidationNotes() {
        return documentValidationNotes;
    }

    public void setDocumentValidationNotes(String documentValidationNotes) {
        this.documentValidationNotes = documentValidationNotes;
    }
}
