package pl.projekt_inzynierski.report;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.projekt_inzynierski.chat.ChatMessage;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class ReportService {

    private final ReportRepository reportRepository;

    public ReportService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }


    List<Report> getReportsWithoutEmployee() {
        return reportRepository.findAllByAssignedUserIsNull().stream()
                .filter(report -> report.getStatus() != ReportStatus.COMPLETED)
                .collect(Collectors.toList());
    }

    @Transactional
    public void addNewMessage(ChatMessage message) {
        Report report = reportRepository.findById(message.getReportId()).orElseThrow();
        report.getMessages().add(message);
    }

    List<Report> getAllReports() {
        return StreamSupport.stream(reportRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    List<Report> getAllReportsByAssignedEmployeeEmail(String email) {
        return reportRepository.findAllByAssignedUser_Email(email);
    }

    List<Report> getAllReportsByReportingUserEmail(String email) {
        return reportRepository.findAllByReportingUser_Email(email);
    }
}
