package pl.projekt_inzynierski.summary;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pl.projekt_inzynierski.report.Report;
import pl.projekt_inzynierski.report.ReportCategory;
import pl.projekt_inzynierski.report.ReportService;
import pl.projekt_inzynierski.report.ReportStatus;
import pl.projekt_inzynierski.user.User;
import pl.projekt_inzynierski.user.UserManagementService;
import pl.projekt_inzynierski.user.UserRepository;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;


@Controller
public class SummaryController {

    private final ReportService reportService;
    private final PdfSummaryGenerator pdfSummaryGenerator;
    private final UserRepository userRepository;
    private final UserManagementService userManagementService;

    public SummaryController(ReportService reportService, PdfSummaryGenerator pdfSummaryGenerator, UserRepository userRepository, UserManagementService userManagementService) {
        this.reportService = reportService;
        this.pdfSummaryGenerator = pdfSummaryGenerator;
        this.userRepository = userRepository;
        this.userManagementService = userManagementService;
    }

    @GetMapping("/summaries")
    public String getSummariesPanel(Model model) {
        model.addAttribute("categories", ReportCategory.values());
        model.addAttribute("statusList", ReportStatus.values());
        if(currentUserIsAdmin()) {
            model.addAttribute("employees", userRepository.findAllByRoles_Name("EMPLOYEE"));
        } else {
            String currentUser = userRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow().getEmail();
            model.addAttribute("currentEmployee", currentUser);
        }
        return "summaries-panel";
    }

    private boolean currentUserIsAdmin() {
        List<String> currentUserRoles = SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream().
                map(GrantedAuthority::getAuthority).toList();
        return currentUserRoles.contains("ROLE_ADMINISTRATOR");
    }

    @PostMapping("/generate-summary")
    public ResponseEntity<?> generateSummary(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam (required = false)LocalDate dateTo,
            @RequestParam(required = false) ReportCategory category, @RequestParam(required = false) ReportStatus status,
            @RequestParam (required = false, defaultValue = "all") String user, @RequestParam String sort) {

        LocalDate[] localDates = checkAnyDateIsNull(dateFrom, dateTo);
        dateFrom = localDates[0];
        dateTo = localDates[1];
        List<Report> reports = reportService.getReportsByDateRange(dateFrom.atStartOfDay(), dateTo.atTime(LocalTime.MAX));
        if(category != null || status != null) {
            if(category != null && status != null) {
                List<Report> reportsByCategoryAndStatus = reportService.getReportsByCategoryAndStatus(category, status);
                reports.retainAll(reportsByCategoryAndStatus);
            } else if(category != null) {
                List<Report> reportsByCategory = reportService.filterReportsByCategory(category);
                reports.retainAll(reportsByCategory);
            } else {
                List<Report> reportsByStatus = reportService.filterReportsByStatus(status);
                reports.retainAll(reportsByStatus);
            }
        }

        Optional<User> userOpt = userManagementService.findUserByEmail(user);
        if(userOpt.isPresent()) {
            reports = reportService.filterReportsByAssignedEmployee(reports, userOpt.get());
        }
        String sortedBy = reportService.sortReports(reports, sort);

        String dateRange = dateFrom + " - " + dateTo;
        String currentUserName = SecurityContextHolder.getContext().getAuthentication().getName();
        try {
            byte[] summary = pdfSummaryGenerator.generateTicketSummary(reports, currentUserName, dateRange,
                    category, status, user, sortedBy);

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_PDF);
            LocalDateTime actualDateTime = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
            String fileName = "REPORT_" + actualDateTime.format(formatter);
            httpHeaders.setContentDispositionFormData("summary", fileName);
            return new ResponseEntity<>(summary, httpHeaders, HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private LocalDate[] checkAnyDateIsNull(LocalDate dateFrom, LocalDate dateTo) {
        if (dateFrom == null) {
            dateFrom = LocalDate.of(1970, 1,1);
        }
        if (dateTo == null) {
            dateTo = LocalDate.now();
        }
        return new LocalDate[] {dateFrom, dateTo};
    }
}
