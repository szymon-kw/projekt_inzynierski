package pl.projekt_inzynierski.summary;

import com.lowagie.text.pdf.BaseFont;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextFontResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;
import pl.projekt_inzynierski.report.*;
import pl.projekt_inzynierski.user.User;
import pl.projekt_inzynierski.user.UserManagementService;
import pl.projekt_inzynierski.user.UserRepository;

import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.StreamSupport;


@Controller
public class SummaryController {

    private final ReportService reportService;
    private final TemplateEngine templateEngine;
    private final UserRepository userRepository;
    private final UserManagementService userManagementService;
    private final ChartService chartService;
    private final ReportCategoryService reportCategoryService;

    public SummaryController(ReportService reportService, TemplateEngine templateEngine, UserRepository userRepository, UserManagementService userManagementService, ChartService chartService, ReportCategoryService reportCategoryService) {
        this.reportService = reportService;
        this.templateEngine = templateEngine;
        this.userRepository = userRepository;
        this.userManagementService = userManagementService;
        this.chartService = chartService;
        this.reportCategoryService = reportCategoryService;
    }

    @GetMapping("/summaries")
    public String getSummariesPanel(Model model) {
        List<String> categories = StreamSupport.stream(reportCategoryService.getAllReportCategories().spliterator(), false)
                .map(Report_Category::getName).toList();
        model.addAttribute("categories", categories);
        model.addAttribute("statusList", ReportStatus.values());
        if (currentUserIsAdmin()) {
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
        public void generateSummary(
                @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam(required = false) LocalDate dateTo,
                @RequestParam(required = false) String categoryName, @RequestParam(required = false) ReportStatus status,
                @RequestParam(required = false, defaultValue = "all") String user, @RequestParam String sort,
                @RequestParam(required = false) boolean showReportsTable, @RequestParam(required = false) boolean showCharts,
                HttpServletResponse response) {

            Report_Category category = reportCategoryService.findByName(categoryName).orElse(null);
            LocalDate[] localDates = checkAnyDateIsNull(dateFrom, dateTo);
            dateFrom = localDates[0];
            dateTo = localDates[1];
            List<Report> reports = reportService.getReportsByDateRange(dateFrom.atStartOfDay(), dateTo.atTime(LocalTime.MAX));
            List<Report> allFoundReports = List.copyOf(reports);
            if (category != null || status != null) {
                if (category != null && status != null) {
                    List<Report> reportsByCategoryAndStatus = reportService.getReportsByCategoryAndStatus(category, status);
                    reports.retainAll(reportsByCategoryAndStatus);
                } else if (category != null) {
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


            //Rozdzielenie adresow email potrzebne do poprawengo wyswietlania ich w raporcie pdf
            Map<Long, String> reportingUserEmailLocalPart = new HashMap<>();
            Map<Long, String> reportingUserEmailDomain = new HashMap<>();
            Map<Long, String> assignedUserEmailLocalPart = new HashMap<>();
            Map<Long, String> assignedUserEmailDomain = new HashMap<>();

            for (Report report : reports) {
                Map<String, String> reportingEmailParts = splitEmail(report.getReportingUser().getEmail());
                reportingUserEmailLocalPart.put(report.getId(), reportingEmailParts.get("localPart"));
                reportingUserEmailDomain.put(report.getId(), reportingEmailParts.get("domain"));

                if (report.getAssignedUser() != null) {
                    Map<String, String> assignedEmailParts = splitEmail(report.getAssignedUser().getEmail());
                    assignedUserEmailLocalPart.put(report.getId(), assignedEmailParts.get("localPart"));
                    assignedUserEmailDomain.put(report.getId(), assignedEmailParts.get("domain"));
                }
            }

            Context context = new Context();
            String summaryHeader = user.equals("all") ?"Raport dotyczący zgłoszeń" : "Raport dotyczący pracownika %s".formatted(user);
            context.setVariable("summaryHeader", summaryHeader);
            context.setVariable("reports", reports);
            Map<Long, String> formattedDates = getFormattedDates(reports);
            context.setVariable("formattedDates", formattedDates);
            context.setVariable("currentDate", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            context.setVariable("currentUserName", SecurityContextHolder.getContext().getAuthentication().getName());
            String dateRange = dateFrom + " - " + dateTo;
            context.setVariable("dateRange", dateRange);
            String categorName = category != null ? category.getName() : "Wszystkie";
            context.setVariable("categories", categorName);
            String statusName = status != null ? status.description : "Wszystkie";
            context.setVariable("status", statusName);
            context.setVariable("sort", sortedBy);
            if(showReportsTable) {
                String[] remainingTime = calculateRemainingTime(reports);
                context.setVariable("remainingTime", remainingTime);
                context.setVariable("reportingUserEmailLocalPart", reportingUserEmailLocalPart);
                context.setVariable("reportingUserEmailDomain", reportingUserEmailDomain);
                context.setVariable("assignedUserEmailLocalPart", assignedUserEmailLocalPart);
                context.setVariable("assignedUserEmailDomain", assignedUserEmailDomain);
                context.setVariable("showReportsTable", showReportsTable);
            }
            if(showCharts) {
                context.setVariable("showCharts", showCharts);
                String categoryChart = null;
                String statusChart = null;
                String avgTimesChartData = null;
                Map<LocalDate, Double> firstReactionTimes = reportService.getAverageFirstReactionTimesForReports(reports, dateFrom, dateTo);
                Map<LocalDate, Double> completionTimes = reportService.getAverageCompletionTimesForReports(reports, dateFrom, dateTo);
                //Jeśli generujemy raport pojedynczego pracownika to na wykresie średnich czasów reakcji,obslugi zamiast
                //średnich czasów pierwszej reakcji na zgłoszenie w danym miesiącu będzie średni czas obsłużenia zgloszen
                //przez wszystkich pracownikow
                boolean singleEmployee = !(user.equals("all"));
                if(singleEmployee) {
                    firstReactionTimes = reportService.getAverageCompletionTimesForReports(allFoundReports, dateFrom, dateTo);
                }
                try {
                    categoryChart = chartService.generateDistributionChart(reports, dateFrom, dateTo, true);
                    statusChart = chartService.generateDistributionChart(reports, dateFrom, dateTo, false);
                    avgTimesChartData = chartService.generateAverageTimeComparisonChart(firstReactionTimes, completionTimes, dateFrom, dateTo, singleEmployee);
                    context.setVariable("averageCompletionTimeChart", avgTimesChartData);
                    context.setVariable("categoryReportChart", categoryChart);
                    context.setVariable("statusReportChart", statusChart);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            String htmlContent = templateEngine.process("summary-template", context);

            response.setContentType("application/pdf");
            DateTimeFormatter fileNameFormatter = DateTimeFormatter.ofPattern("yyyy_MM_dd, HH:mm:ss");
            String formattedFileName = "REPORT_" + LocalDateTime.now().format(fileNameFormatter);
            response.setHeader("Content-Disposition", "attachment; filename=\"" + formattedFileName + ".pdf\"");

            OutputStream outputStream = null;
            try {
                outputStream = response.getOutputStream();
                ITextRenderer renderer = new ITextRenderer();
                renderer.setDocumentFromString(htmlContent);
                ITextFontResolver fontResolver = renderer.getFontResolver();
                fontResolver.addFont(Objects.requireNonNull(getClass().getResource("/fonts/Roboto-Regular.ttf")).toString(),
                        BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
                renderer.layout();
                renderer.createPDF(outputStream);
                outputStream.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    private String[] calculateRemainingTime(List<Report> reports) {
        String[] remainingTime = new String[reports.size()];
        for (int i = 0; i < reports.size(); i++) {
            boolean forFirstRespond = reports.get(i).getStatus() == ReportStatus.PENDING;
            String remainingTimeTemplate = reports.get(i).getRemainingTime(forFirstRespond).getDays() > 0 ? (reports.get(i).getRemainingTime(forFirstRespond).getDays() + "d ") : "";
            remainingTimeTemplate += reports.get(i).getRemainingTime(forFirstRespond).getHours() > 0 ? (reports.get(i).getRemainingTime(forFirstRespond).getHours() + "godz. ") : "";
            remainingTimeTemplate += reports.get(i).getRemainingTime(forFirstRespond).getMinutes() > 0 ? (reports.get(i).getRemainingTime(forFirstRespond).getMinutes() + "min.") : "";
            remainingTime[i] = (reports.get(i).getStatus() == ReportStatus.COMPLETED || (reports.get(i).getRemainingTime(forFirstRespond).getDays() <= 0  &&
                    reports.get(i).getRemainingTime(forFirstRespond).getHours() <= 0 && reports.get(i).getRemainingTime(forFirstRespond).getMinutes() <= 0))? "-": remainingTimeTemplate;
        }
        return remainingTime;
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

    private Map<String, String> splitEmail(String email) {
        Map<String, String> emailParts = new HashMap<>();
        if (email != null && email.contains("@")) {
            String[] parts = email.split("@");
            emailParts.put("localPart", parts[0]);
            emailParts.put("domain", "@" + parts[1]);
        } else {
            emailParts.put("localPart", email != null ? email : "");
            emailParts.put("domain", "");
        }
        return emailParts;
    }

    private Map<Long, String> getFormattedDates(List<Report> reports) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        Map<Long, String> formattedDates = new HashMap<>();
        for (Report report : reports) {
            formattedDates.put(report.getId(), report.getDateAdded().format(formatter));
        }
        return formattedDates;
    }

}
