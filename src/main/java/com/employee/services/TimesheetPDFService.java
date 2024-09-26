package com.employee.services;

import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.employee.entities.EmployeeTimesheet;
import com.employee.repositories.TimesheetRepository;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;

@Service
public class TimesheetPDFService {

    @Autowired
    private TimesheetRepository timesheetRepository;

    public void generateMonthlyReport(String employeeId, String pdfPath) throws FileNotFoundException {
        LocalDate now = LocalDate.now();
        LocalDate startDate = now.withDayOfMonth(1);
        LocalDate endDate = now.withDayOfMonth(now.lengthOfMonth());

        List<EmployeeTimesheet> timesheets = timesheetRepository.findByEmployeeIdAndDateBetween(employeeId, startDate, endDate);

        PdfWriter writer = new PdfWriter(pdfPath);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);

        document.add(new Paragraph("Monthly Timesheet Report for Employee ID: " + employeeId));

        float[] columnWidths = {1, 2, 1, 3, 3, 3, 2};
        Table table = new Table(columnWidths);

        table.addCell(new Cell().add(new Paragraph("S.No")));
        table.addCell(new Cell().add(new Paragraph("Date")));
        table.addCell(new Cell().add(new Paragraph("Hours")));
        table.addCell(new Cell().add(new Paragraph("Project")));
        table.addCell(new Cell().add(new Paragraph("Task")));
        table.addCell(new Cell().add(new Paragraph("Activity")));
        table.addCell(new Cell().add(new Paragraph("Remarks")));

        int rowCount = 0;
        for (EmployeeTimesheet timesheet : timesheets) {
            rowCount++;
            table.addCell(new Cell().add(new Paragraph(String.valueOf(rowCount))));
            table.addCell(new Cell().add(new Paragraph(timesheet.getDate().toString())));
            table.addCell(new Cell().add(new Paragraph(String.valueOf(timesheet.getHoursWorked()))));
            table.addCell(new Cell().add(new Paragraph(timesheet.getProject())));
            table.addCell(new Cell().add(new Paragraph(timesheet.getTask())));
            table.addCell(new Cell().add(new Paragraph(timesheet.getActivity())));
            table.addCell(new Cell().add(new Paragraph(timesheet.getRemarks())));
        }

        document.add(table);
        document.close();

        System.out.println("PDF generated at " + pdfPath);
    }
}