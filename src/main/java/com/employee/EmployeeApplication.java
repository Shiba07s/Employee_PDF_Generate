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
		
		 // Keep the application alive (this is just an example)
        try {
            Thread.sleep(Long.MAX_VALUE);  // Keeps running indefinitely
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
	}
	 @Override
	    public void run(String... args) throws Exception {
	        String employeeId = "10267";
	        String pdfPath = "monthly_timesheet_report.pdf";
	        pdfService.generateMonthlyReport(employeeId, pdfPath);
	    }

}
