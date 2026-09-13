package com.hrthub.newhire.controller;

import com.hrthub.newhire.dto.EmployeeResponse;
import com.hrthub.newhire.dto.NewHireRequest;
import com.hrthub.newhire.entity.Employee;
import com.hrthub.newhire.service.NewHireOnboardingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
@CrossOrigin(origins = "*")
public class EmployeeController {

    private final NewHireOnboardingService onboardingService;

    public EmployeeController(NewHireOnboardingService onboardingService) {
        this.onboardingService = onboardingService;
    }

    @PostMapping
    public ResponseEntity<EmployeeResponse> onboard(@Valid @RequestBody NewHireRequest request) {
        Employee employee = onboardingService.onboard(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(new EmployeeResponse(employee));
    }

    @GetMapping
    public List<EmployeeResponse> findAll() {
        return onboardingService.findAll().stream().map(EmployeeResponse::new).toList();
    }

    @GetMapping("/{id}")
    public EmployeeResponse findById(@PathVariable Long id) {
        return new EmployeeResponse(onboardingService.findById(id));
    }
}
