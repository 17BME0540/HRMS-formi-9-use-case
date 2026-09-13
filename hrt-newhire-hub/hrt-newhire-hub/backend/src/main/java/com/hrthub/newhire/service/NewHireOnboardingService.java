package com.hrthub.newhire.service;

import com.hrthub.newhire.dto.NewHireRequest;
import com.hrthub.newhire.entity.Employee;
import com.hrthub.newhire.repository.EmployeeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class NewHireOnboardingService {

    private final EmployeeRepository employeeRepository;
    private final I9VerificationService i9VerificationService;
    private final DocumentValidator documentValidator;

    public NewHireOnboardingService(
            EmployeeRepository employeeRepository,
            I9VerificationService i9VerificationService,
            DocumentValidator documentValidator) {
        this.employeeRepository = employeeRepository;
        this.i9VerificationService = i9VerificationService;
        this.documentValidator = documentValidator;
    }

    public Employee onboard(NewHireRequest request) {
        Employee employee = new Employee(request.getName(), request.getHireDate(), request.getDocumentText());

        employee.setI9Status(i9VerificationService.verify(request.getName(), request.getHireDate()));

        DocumentValidationResult validationResult = documentValidator.validate(request.getDocumentText());
        employee.setDocumentValidationStatus(validationResult.status());
        employee.setDocumentValidationNotes(validationResult.notes());

        return employeeRepository.save(employee);
    }

    public List<Employee> findAll() {
        return employeeRepository.findAll();
    }

    public Employee findById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No employee found with id " + id));
    }
}
