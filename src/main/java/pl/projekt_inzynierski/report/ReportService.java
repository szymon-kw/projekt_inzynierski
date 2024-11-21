package pl.projekt_inzynierski.report;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.projekt_inzynierski.chat.ChatMessage;
import pl.projekt_inzynierski.user.User;
import pl.projekt_inzynierski.user.UserRepository;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class ReportService {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;

    public ReportService(UserRepository userRepository, ReportRepository reportRepository) {
        this.userRepository = userRepository;
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

    @Transactional
    public void assignEmployeeToReport(Long reportId, Long employeeId) {
        Report report = reportRepository.findById(reportId).orElseThrow(() -> new IllegalArgumentException("Report not found"));
        User employee = userRepository.findById(employeeId).orElseThrow(() -> new IllegalArgumentException("Employee not found"));

        if (!employee.getRoles().stream().anyMatch(role -> role.getName().equals("EMPLOYEE"))) {
            throw new IllegalArgumentException("User is not an employee");
        }

        report.setAssignedUser(employee);
        reportRepository.save(report);
    }
    @Transactional
    public void saveNewReport(Report report) {
        reportRepository.save(report);
    }


}
