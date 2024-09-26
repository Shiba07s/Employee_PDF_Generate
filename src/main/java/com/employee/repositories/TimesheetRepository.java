package com.employee.repositories;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.employee.entities.EmployeeTimesheet;

public interface TimesheetRepository extends JpaRepository<EmployeeTimesheet, Integer> {
    List<EmployeeTimesheet> findByEmployeeIdAndDateBetween(String employeeId, LocalDate startDate, LocalDate endDate);

	Optional<EmployeeTimesheet> findByEmployeeId(String employeeId);
}