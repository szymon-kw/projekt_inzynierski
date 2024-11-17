package pl.projekt_inzynierski.report;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pl.projekt_inzynierski.user.User;
import pl.projekt_inzynierski.user.UserRepository;
import pl.projekt_inzynierski.user.UserRole;

import java.util.Set;
import java.util.stream.Collectors;

@Controller
public class ReportController {

    private final UserRepository userRepository;
    private final ReportService reportService;

    public ReportController(UserRepository userRepository,ReportService reportService) {
        this.userRepository = userRepository;
        this.reportService = reportService;
    }

    @GetMapping("/reports")
    String getAllReports(Authentication authentication, Model model) {
        String currentUserName = authentication.getName();
        User currentUser = userRepository.findByEmail(currentUserName).orElseThrow();
        Set<String> currentUserRoles = currentUser.getRoles().stream()
                .map(UserRole::getName)
                .collect(Collectors.toSet());
        if (currentUserRoles.contains("ADMINISTRATOR")) {
            model.addAttribute("reports", reportService.getAllReports());
            model.addAttribute("employees", userRepository.findAllUserByRolesName("EMPLOYEE")); //+++++ADMIN
        } else if(currentUserRoles.contains("EMPLOYEE")) {
            model.addAttribute("reports", reportService.getAllReportsByAssignedEmployeeEmail(currentUserName));
        }
        else {
            model.addAttribute("reports", reportService.getAllReportsByReportingUserEmail(currentUserName));
        }
        return "report-listing";
    }

    @PostMapping("/reports/assign")
    String assignEmployeeToReport(@RequestParam Long reportId, @RequestParam Long employeeId) {
        reportService.assignEmployeeToReport(reportId, employeeId);
        return "redirect:/reports";
    }

}
