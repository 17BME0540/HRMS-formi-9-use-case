package com.hrthub.newhire.repository;

import com.hrthub.newhire.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
}
