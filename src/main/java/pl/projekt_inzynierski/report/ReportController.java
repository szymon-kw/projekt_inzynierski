package pl.projekt_inzynierski.report;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import pl.projekt_inzynierski.Dto.FinalListViewDto;
import pl.projekt_inzynierski.user.UserRepository;

import java.util.Set;
import java.util.stream.Collectors;

@Controller
public class ReportController {


    private final ReportService reportService;

    @Autowired
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/reports")
    String getAllReports() {
        return "report-listing";
    }

    @PostMapping("/reports/assign")
    String assignEmployeeToReport(@RequestParam Long reportId, @RequestParam Long employeeId, Authentication authentication) {
        if (havePermission(authentication, false)) {
            reportService.assignEmployeeToReport(reportId, employeeId);
            reportService.changeReportStatus(reportId, ReportStatus.UNDER_REVIEW);
        }
        return "redirect:/chat?reportId=" + reportId;
    }

    @GetMapping("/reports/close")
    public String closeReport(@RequestParam Long reportId, Authentication authentication) {

        if (havePermission(authentication, true)){
            reportService.closeReport(reportId);
        }

        return "redirect:/chat?reportId=" + reportId;
    }

    @GetMapping("/reports/delete")
    public String deleteReport(@RequestParam Long reportId, Authentication authentication) {
        if (havePermission(authentication, false)) {
            reportService.deleteReport(reportId);
        }
        return "redirect:/reports";
    }

    @GetMapping("/api/reports")
    @ResponseBody
    FinalListViewDto grtReportsInfo(Authentication authentication,
                                    @RequestParam int Page,
                                    @RequestParam int PageSize,
                                    @RequestParam String ListCategory,
                                    @RequestParam(required = false) String Search,
                                    @RequestParam(required = false) String SortBy,
                                    @RequestParam(required = false) String SortOrder) {

        Set<String> userRole = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
        String UserName = authentication.getName();



        if (userRole.contains("ROLE_ADMINISTRATOR")) {
            return reportService.prepareListForAdmins(Page, PageSize, ListCategory, Search, SortBy, SortOrder);
        } else if (userRole.contains("ROLE_EMPLOYEE")) {
            return reportService.prepareListForEmployees(UserName, Page, PageSize, ListCategory, Search, SortBy, SortOrder);
        }else {
            return reportService.prepareListForUsers(UserName, Page, PageSize, ListCategory, Search, SortBy, SortOrder);
        }

    }
    private boolean havePermission(Authentication authentication, boolean forEmployeeTo) {

        Set<String> userRole = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());

        if(forEmployeeTo) {
            return userRole.contains("ROLE_ADMINISTRATOR") || userRole.contains("ROLE_EMPLOYEE");
        }else{
            return userRole.contains("ROLE_ADMINISTRATOR");
        }
    }



}
