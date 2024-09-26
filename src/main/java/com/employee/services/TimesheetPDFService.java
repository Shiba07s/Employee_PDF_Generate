package com.employee.services;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.format.DateTimeFormatter;
import java.time.LocalDate;
import java.time.DayOfWeek;
import java.util.List;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.colors.Color;

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

    public void generateMonthlyReport(String employeeId, String employeeName, int year, int month, String pdfPath) throws FileNotFoundException {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
        List<EmployeeTimesheet> timesheets = timesheetRepository.findByEmployeeIdAndDateBetween(employeeId, startDate, endDate);

        // Ensure a unique file name
        pdfPath = ensureUniqueFileName(pdfPath);

        PdfWriter writer = new PdfWriter(pdfPath);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);

        document.add(new Paragraph(employeeId + " " + employeeName + " Timesheet"));

        float[] columnWidths = {1, 2, 1, 3, 3, 3, 2};
        Table table = new Table(columnWidths);

        table.addHeaderCell(new Cell().add(new Paragraph("S.No")));
        table.addHeaderCell(new Cell().add(new Paragraph("Date")));
        table.addHeaderCell(new Cell().add(new Paragraph("Hours")));
        table.addHeaderCell(new Cell().add(new Paragraph("Project")));
        table.addHeaderCell(new Cell().add(new Paragraph("Task")));
        table.addHeaderCell(new Cell().add(new Paragraph("Activities")));
        table.addHeaderCell(new Cell().add(new Paragraph("Remarks")));

        int rowCount = 0;
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            rowCount++;
            EmployeeTimesheet timesheet = findTimesheetForDate(timesheets, date);
            
            Cell[] cells = new Cell[7];
            for (int i = 0; i < 7; i++) {
                cells[i] = new Cell();
            }

            cells[0].add(new Paragraph(String.valueOf(rowCount)));
            cells[1].add(new Paragraph(date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))));
            
            if (timesheet != null) {
                cells[2].add(new Paragraph(String.valueOf(timesheet.getHoursWorked())));
                cells[3].add(new Paragraph(timesheet.getProject()));
                cells[4].add(new Paragraph(timesheet.getTask()));
                cells[5].add(new Paragraph(timesheet.getActivity()));
                cells[6].add(new Paragraph(timesheet.getRemarks()));
            }

            // Set background color based on day type
            Color backgroundColor = determineBackgroundColor(date, timesheet);
            for (Cell cell : cells) {
                cell.setBackgroundColor(backgroundColor);
                table.addCell(cell);
            }
        }

        document.add(table);
        document.close();
        System.out.println("PDF generated at " + pdfPath);
    }

    private String ensureUniqueFileName(String pdfPath) {
        File file = new File(pdfPath);
        if (!file.exists()) {
            return pdfPath;
        }

        String name = pdfPath.substring(0, pdfPath.lastIndexOf("."));
        String extension = pdfPath.substring(pdfPath.lastIndexOf("."));
        int counter = 1;

        while (file.exists()) {
            pdfPath = name + "_" + counter + extension;
            file = new File(pdfPath);
            counter++;
        }

        return pdfPath;
    }

    private EmployeeTimesheet findTimesheetForDate(List<EmployeeTimesheet> timesheets, LocalDate date) {
        return timesheets.stream()
                .filter(ts -> ts.getDate().equals(date))
                .findFirst()
                .orElse(null);
    }

    private Color determineBackgroundColor(LocalDate date, EmployeeTimesheet timesheet) {
        if (date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY) {
            return ColorConstants.LIGHT_GRAY;
        } else if (timesheet == null || "0".equals(timesheet.getHoursWorked())) { // Check if hoursWorked is "0"
            return ColorConstants.YELLOW; // Full day leave
        } else if ("4".equals(timesheet.getHoursWorked())) { // Check if hoursWorked is "4"
            return ColorConstants.CYAN; // Half day leave
        } else if ("Holiday".equals(timesheet.getHoursWorked())) { // Check for holidays
            return new DeviceRgb(128, 0, 128); // Purple color
        }
        return ColorConstants.WHITE;
    }
}
