package com.employee.services;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.employee.entities.EmployeeTimesheet;
import com.employee.repositories.TimesheetRepository;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;

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

//        document.add(new Paragraph(employeeId + " " + employeeName + " Timesheet"));
//        Paragraph headerParagraph = new Paragraph(employeeId + " " + employeeName + " Timesheet")
//                .setTextAlignment(TextAlignment.CENTER);
        
        Paragraph headerParagraph = new Paragraph(employeeId + " " + employeeName + " Timesheet")
                .setFontSize(18)  // Increase the font size for a headline look
                .setBold()        // Make the text bold
                .setFontColor(ColorConstants.BLUE)  // Set the font color to blue
                .setTextAlignment(TextAlignment.CENTER)  // Center the text
                .setBorderBottom(new SolidBorder(ColorConstants.GRAY, 2))  // Add a bottom border
                .setMarginBottom(10)  // Add margin below the header
                .setMarginTop(20)  // Add margin above the header for spacing
                .setPadding(5);    // Add padding for a better look

        document.add(headerParagraph);
        float[] columnWidths = {1, 2, 1, 3, 3, 3, 2};
        Table table = new Table(columnWidths);
        
        Color headerColor = new DeviceRgb(221,240,246); // #D0EDDB
        Color redFontColor = ColorConstants.RED; // Red color for font

        table.addHeaderCell(new Cell().add(new Paragraph("S.No")).setBackgroundColor(headerColor).setFontColor(redFontColor));
        table.addHeaderCell(new Cell().add(new Paragraph("Date")).setBackgroundColor(headerColor).setFontColor(redFontColor));
        table.addHeaderCell(new Cell().add(new Paragraph("Hours")).setBackgroundColor(headerColor).setFontColor(redFontColor));
        table.addHeaderCell(new Cell().add(new Paragraph("Project")).setBackgroundColor(headerColor).setFontColor(redFontColor));
        table.addHeaderCell(new Cell().add(new Paragraph("Task")).setBackgroundColor(headerColor).setFontColor(redFontColor));
        table.addHeaderCell(new Cell().add(new Paragraph("Activities")).setBackgroundColor(headerColor).setFontColor(redFontColor));
        table.addHeaderCell(new Cell().add(new Paragraph("Remarks")).setBackgroundColor(headerColor).setFontColor(redFontColor));

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
            return new DeviceRgb(204,204,204);
        } else if (timesheet == null || "0".equals(timesheet.getHoursWorked())) { // Check if hoursWorked is "0"
        	return new DeviceRgb(250,236,209);
        } else if ("4".equals(timesheet.getHoursWorked())) { // Check if hoursWorked is "4"
        	return new DeviceRgb(208, 237, 219);
        } else if ("Holiday".equals(timesheet.getHoursWorked())) {  
            return new DeviceRgb(221,240,246);  
        }
        return ColorConstants.WHITE;
    }
}
