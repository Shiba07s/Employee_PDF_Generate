package com.employee.repositories;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.employee.entities.EmployeeTimesheet;

public interface TimesheetRepository extends JpaRepository<EmployeeTimesheet, Long> {
    List<EmployeeTimesheet> findByEmployeeIdAndDateBetween(String employeeId, LocalDate startDate, LocalDate endDate);
}