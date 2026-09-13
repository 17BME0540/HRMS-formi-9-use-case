package com.hrthub.newhire.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hrthub.newhire.dto.NewHireRequest;
import com.hrthub.newhire.entity.DocumentValidationStatus;
import com.hrthub.newhire.entity.Employee;
import com.hrthub.newhire.entity.I9Status;
import com.hrthub.newhire.service.NewHireOnboardingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.lang.reflect.Field;
import java.time.LocalDate;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EmployeeController.class)
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private NewHireOnboardingService onboardingService;

    @Test
    void onboardReturns201WithI9AndDocumentStatus() throws Exception {
        Employee saved = buildEmployee(1L, "Jane Doe", I9Status.VERIFIED, DocumentValidationStatus.COMPLETE);
        when(onboardingService.onboard(org.mockito.ArgumentMatchers.any())).thenReturn(saved);

        NewHireRequest request = new NewHireRequest();
        request.setName("Jane Doe");
        request.setHireDate(LocalDate.now().minusDays(1));
        request.setDocumentText("Name: Jane Doe\nSSN last 4: 1234\nDate: 2026-09-01");

        mockMvc.perform(post("/api/employees")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Jane Doe"))
                .andExpect(jsonPath("$.i9Status").value("VERIFIED"))
                .andExpect(jsonPath("$.documentValidationStatus").value("COMPLETE"));
    }

    @Test
    void onboardReturns400WhenNameMissing() throws Exception {
        NewHireRequest request = new NewHireRequest();
        request.setHireDate(LocalDate.now());
        request.setDocumentText("some text");

        mockMvc.perform(post("/api/employees")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void findAllReturnsEmployees() throws Exception {
        Employee employee = buildEmployee(2L, "John Smith", I9Status.PENDING, DocumentValidationStatus.INCOMPLETE);
        when(onboardingService.findAll()).thenReturn(java.util.List.of(employee));

        mockMvc.perform(get("/api/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("John Smith"))
                .andExpect(jsonPath("$[0].i9Status").value("PENDING"));
    }

    /**
     * Employee's setters for id/status are package-private-by-design (id is
     * JPA-generated); reflection keeps this test file focused on HTTP
     * behavior rather than adding test-only production setters.
     */
    private Employee buildEmployee(Long id, String name, I9Status i9Status, DocumentValidationStatus docStatus) {
        Employee employee = new Employee(name, LocalDate.now().minusDays(1), "doc text");
        employee.setI9Status(i9Status);
        employee.setDocumentValidationStatus(docStatus);
        employee.setDocumentValidationNotes("test notes");
        setId(employee, id);
        return employee;
    }

    private void setId(Employee employee, Long id) {
        try {
            Field field = Employee.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(employee, id);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }
}
