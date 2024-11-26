package pl.projekt_inzynierski.report;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.projekt_inzynierski.chat.ChatMessage;
import pl.projekt_inzynierski.user.User;
import pl.projekt_inzynierski.user.UserRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class ReportService {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;

    public ReportService(ReportRepository reportRepository, UserRepository userRepository) {
        this.reportRepository = reportRepository;
        this.userRepository = userRepository;
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



    public List<Report> getReportsByCategory(ReportCategory reportCategory) {
        return reportRepository.findAllByCategory(reportCategory);
    }

    public List<Report> getReportsByDateRange(LocalDateTime dateFrom, LocalDateTime dateTo) {
        return reportRepository.findAllByDateAddedIsBetween(dateFrom, dateTo);
    }

    public List<Report> filterReportsByCategory(ReportCategory category) {
        return filterReports(report -> report.getCategory() == category);
    }

    public List<Report> filterReportsByStatus(ReportStatus status) {
        return filterReports(report -> report.getStatus() == status);
    }

    public List<Report> filterReportsByAssignedEmployee(List<Report> reports, User employee) {
        return reports.stream()
                .filter(report -> report.getAssignedUser() == employee)
                .collect(Collectors.toList());
    }

    public List<Report> getReportsByCategoryAndStatus(ReportCategory category, ReportStatus status) {
        List<Report> reportsByCategory = filterReportsByCategory(category);
        List<Report> reportsByStatus = filterReportsByStatus(status);
        reportsByCategory.retainAll(reportsByStatus);
        return reportsByCategory;
    }

    private List<Report> filterReports(Predicate<Report> predicate) {
        return getAllReports().stream()
                .filter(predicate)
                .collect(Collectors.toList());
    }

    public String sortReports(List<Report> reports, String sort) {
        return switch (sort) {
            case "addedDateDesc" -> {
                reports.sort(Comparator.comparing(Report::getDateAdded).reversed());
                yield "Data zgłoszenia (od najnowszych)";
            }
            case "addedDateAsc" -> {
                reports.sort(Comparator.comparing(Report::getDateAdded));
                yield "Data zgłoszenia (od najstarszych)";
            }
            case "remainingTimeAsc" -> {
                reports.sort(Comparator.comparing(Report::getRemainingTimeDuration));
                yield "Pozostały czas do końca (rosnąco)";
            }
            case "remainingTimeDesc" -> {
                reports.sort(Comparator.comparing(Report::getRemainingTimeDuration).reversed());
                yield "Pozostały czas do końca (malejąco)";
            }
            default -> "Brak";
        };
    }

    @Transactional
    public void changeReportStatus(Long reportId, ReportStatus status) {
        Report report = reportRepository.findById(reportId).orElseThrow();
        report.setStatus(status);
    }
}
