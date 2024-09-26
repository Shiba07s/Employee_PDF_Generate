package com.employee;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.employee.services.TimesheetPDFService;

@SpringBootApplication
public class EmployeeApplication implements CommandLineRunner {
    
    @Autowired
    private TimesheetPDFService pdfService;

    public static void main(String[] args) {
        SpringApplication.run(EmployeeApplication.class, args);
    }

    public void run(String... args) throws Exception {
        String employeeId = "2009373";
        String employeeName = "Ashutosh";
        int year = 2024;
        int month = 9; // September
        String pdfPath = "2009373_Ashutosh_Timesheet.pdf";
        pdfService.generateMonthlyReport(employeeId, employeeName, year, month, pdfPath);
    }

}