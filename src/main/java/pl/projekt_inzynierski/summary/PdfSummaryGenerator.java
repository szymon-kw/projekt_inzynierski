package pl.projekt_inzynierski.summary;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.springframework.stereotype.Service;
import pl.projekt_inzynierski.report.Report;
import pl.projekt_inzynierski.report.ReportCategory;
import pl.projekt_inzynierski.report.ReportStatus;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class PdfSummaryGenerator {

    public byte[] generateTicketSummary(List<Report> reports, String user, String dateRange,
                                        ReportCategory category, ReportStatus status, String employee, String sort) throws IOException {
        PDDocument document = new PDDocument();
        PDType0Font boldFont = PDType0Font.load(document, new File("ARIALBD 1.TTF"));
        PDType0Font regularFont = PDType0Font.load(document, new File("ARIAL.TTF"));

        int startY = 750;
        int rowsPerPage = 10;

        int currentRow = 0;
        PDPage page = new PDPage();
        document.addPage(page);
        PDPageContentStream contentStream = new PDPageContentStream(document, page);

        try {
            addReportHeader(contentStream, boldFont, user, dateRange, category, status, employee, sort);

            startY -= 180;
            if(!reports.isEmpty()) {
                drawTableHeader(contentStream, boldFont, startY);

                startY -= 30;
                int iterNum = 1;

                for (Report report : reports) {
                    if (currentRow >= rowsPerPage) {
                        contentStream.close();
                        page = new PDPage();
                        document.addPage(page);
                        contentStream = new PDPageContentStream(document, page);
                        startY = 750;
                        currentRow = 0;
                        drawTableHeader(contentStream, boldFont, startY);
                        startY -= 30;
                    }

                    startY -= drawTableRow(contentStream, regularFont, startY, report, iterNum);
                    currentRow++;
                    iterNum++;
                }
            } else {
                contentStream.beginText();
                contentStream.setFont(regularFont, 15);
                contentStream.newLineAtOffset(100, 500);
                contentStream.showText("Nie znaleziono zgłoszeń spełniających powyższe kryteria.");
                contentStream.endText();
            }
        } finally {
            contentStream.close();
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        document.save(outputStream);
        document.close();

        return outputStream.toByteArray();
    }

    private void addReportHeader(PDPageContentStream contentStream, PDType0Font font, String user, String dateRange, ReportCategory category, ReportStatus status, String employee, String sort) throws IOException {
        contentStream.beginText();
        contentStream.setFont(font, 18);
        contentStream.newLineAtOffset(20, 750);
        String summaryHeader = employee.equals("all") ?"Raport dotyczący zgłoszeń" : "Raport dotyczący pracownika %s".formatted(employee);
        contentStream.showText(summaryHeader);
        contentStream.endText();

        contentStream.beginText();
        contentStream.setFont(font, 12);
        contentStream.newLineAtOffset(20, 700);
        contentStream.showText("Wygenerowano: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        contentStream.newLineAtOffset(0, -20);
        contentStream.showText("Wygenerowano przez: " + user);
        contentStream.newLineAtOffset(0, -20);
        contentStream.showText("Przedział dat uwzględniony w raporcie: " + dateRange);
        contentStream.newLineAtOffset(0, -20);
        String categoryName = category != null ? category.description : "Wszystkie";
        contentStream.showText("Kategoria zgłoszeń uwzględniona w raporcie: " + categoryName);
        contentStream.newLineAtOffset(0, -20);
        String statusName = status != null ? status.description : "Wszystkie";
        contentStream.showText("Status zgłoszeń uwzględniony w raporcie: " + statusName);
        contentStream.newLineAtOffset(0, -20);
        contentStream.showText("Posortowano według: " + sort);
        contentStream.endText();
    }

    private void drawTableHeader(PDPageContentStream contentStream, PDType0Font font, int yPosition) throws IOException {
        String[] headers = {"#", "Tytuł", "Kategoria", "Status", "Data zgłoszenia","Pozostały czas", "Zgłaszający", "Przypisany pracownik"};
        int[] columnWidths = {20, 80, 60, 55, 90,75, 95, 100};

        int tableWidth = 0;
        for (int width : columnWidths) {
            tableWidth += width;
        }

        int xPosition = (595 - tableWidth) / 2;
        int padding = 5;
        contentStream.setLineWidth(1);
        contentStream.moveTo(xPosition, yPosition + 5);
        contentStream.lineTo(xPosition + tableWidth, yPosition + 5);
        contentStream.stroke();

        for (int i = 0; i < headers.length; i++) {
            contentStream.beginText();
            contentStream.setFont(font, 9);
            contentStream.newLineAtOffset(xPosition + padding, yPosition - 10);
            contentStream.showText(headers[i]);
            contentStream.endText();
            xPosition += columnWidths[i];

            contentStream.moveTo(xPosition, yPosition + 5);
            contentStream.lineTo(xPosition, yPosition - 15);
            contentStream.stroke();
        }
    }

    private int drawTableRow(PDPageContentStream contentStream, PDType0Font font, int yPosition, Report report, int iterNum) throws IOException {

        String assignedUser = report.getAssignedUser() != null ? report.getAssignedUser().getEmail() : " - ";
        String remainingTime = calculateRemainingTime(report);

        String[] rowData = {
                String.valueOf(iterNum),
                wrapText(report.getTitle(), 15),
                report.getCategory().description,
                wrapText(report.getStatus().description, 15),
                report.getDateAdded().toString(),
                remainingTime,
                wrapEmail(report.getReportingUser().getEmail(), 20),
                wrapEmail(assignedUser, 20)
        };
        int[] columnWidths = {20, 80, 60, 55, 90,75, 95, 100};

        int tableWidth = 0;
        for (int width : columnWidths) {
            tableWidth += width;
        }

        int xPosition = (595 - tableWidth) / 2;
        int padding = 5;
        int cellHeight = yPosition;
        int maxRowHeight = 0;

        for (int i = 0; i < rowData.length; i++) {
            String[] wrappedLines = rowData[i].split("\n");
            int linesInCell = wrappedLines.length;
            maxRowHeight = Math.max(maxRowHeight, linesInCell * 12);

            for (String line : wrappedLines) {
                contentStream.beginText();
                contentStream.setFont(font, 9);
                contentStream.newLineAtOffset(xPosition + padding, cellHeight - 10);
                contentStream.showText(line);
                contentStream.endText();
                cellHeight -= 12;
            }
            xPosition += columnWidths[i];
            cellHeight = yPosition;

            contentStream.moveTo(xPosition, yPosition + 5);
            contentStream.lineTo(xPosition, yPosition - maxRowHeight - 5);
            contentStream.stroke();
        }

        contentStream.setLineWidth(0.5f);
        contentStream.moveTo((595 - tableWidth) / 2, yPosition - maxRowHeight - 5);
        contentStream.lineTo((595 - tableWidth) / 2 + tableWidth, yPosition - maxRowHeight - 5);
        contentStream.stroke();

        return maxRowHeight + 10;
    }

    private static String calculateRemainingTime(Report report) {
        String remainingTimeTemplate = report.getRemainingTime().getDays() > 0 ? (report.getRemainingTime().getDays() + "d ") : "";
        remainingTimeTemplate += report.getRemainingTime().getHours() > 0 ? (report.getRemainingTime().getHours() + "gdz. ") : "";
        remainingTimeTemplate += report.getRemainingTime().getMinutes() > 0 ? (report.getRemainingTime().getMinutes() + "min.") : "";
        String remainingTime = (report.getStatus() == ReportStatus.COMPLETED || (report.getRemainingTime().getDays() <= 0  &&
                report.getRemainingTime().getDays() <= 0 && report.getRemainingTime().getMinutes() <= 0))? "            -": remainingTimeTemplate;
        return remainingTime;
    }


    private String wrapText(String text, int maxChars) {
        StringBuilder wrapped = new StringBuilder();
        String[] words = text.split(" ");
        int lineLength = 0;

        for (String word : words) {
            if (lineLength + word.length() > maxChars) {
                wrapped.append("\n");
                lineLength = 0;
            }
            if (lineLength > 0) {
                wrapped.append(" ");
                lineLength++;
            }
            wrapped.append(word);
            lineLength += word.length();
        }

        return wrapped.toString();
    }

    private String wrapEmail(String email, int maxChars) {
        if (email.length() <= maxChars) {
            return email;
        }

        int atIndex = email.indexOf('@');
        if (atIndex == -1) {
            return wrapText(email, maxChars);
        }

        String localPart = email.substring(0, atIndex);
        String domainPart = email.substring(atIndex);

        if (localPart.length() <= maxChars) {
            return localPart + "\n" + domainPart;
        }

        int lastDotIndex = localPart.lastIndexOf('.');
        if (lastDotIndex != -1 && localPart.length() - lastDotIndex <= maxChars) {
            String beforeDot = localPart.substring(0, lastDotIndex);
            String afterDot = localPart.substring(lastDotIndex);

            if (beforeDot.length() <= maxChars) {
                return beforeDot + "\n" + afterDot + domainPart;
            }
        }

        StringBuilder wrappedEmail = new StringBuilder();
        String[] words = localPart.split("\\b");
        int lineLength = 0;

        for (String word : words) {
            if (lineLength + word.length() > maxChars) {
                wrappedEmail.append("\n");
                lineLength = 0;
            }
            if (lineLength > 0) {
                wrappedEmail.append(" ");
                lineLength++;
            }
            wrappedEmail.append(word);
            lineLength += word.length();
        }

        wrappedEmail.append("\n").append(domainPart);

        return wrappedEmail.toString();
    }
}