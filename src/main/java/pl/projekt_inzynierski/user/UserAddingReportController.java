package pl.projekt_inzynierski.user;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pl.projekt_inzynierski.Dto.NewReportDTO;
import pl.projekt_inzynierski.report.ReportCategoryService;
import pl.projekt_inzynierski.report.Report_Category;

@Controller
@RequestMapping("/new-report")
public class UserAddingReportController {

    private final UserAddingReportService userAddingReportService;
    private final ReportCategoryService reportCategoryService;

    @Autowired
    public UserAddingReportController(UserAddingReportService userAddingReportService, ReportCategoryService reportCategoryService) {
        this.userAddingReportService = userAddingReportService;
        this.reportCategoryService = reportCategoryService;
    }

    @GetMapping("")
    public String newReport(Model model) {
        model.addAttribute("report", new NewReportDTO());
        model.addAttribute("categories", reportCategoryService.getAllReportCategories());
        return "add-new-report";
    }
    @GetMapping("/add")
    public String addReportGet() {
        return "redirect:/new-report";
    }

    @PostMapping(value = "/add", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public String addNewReport(@Valid @ModelAttribute("report") NewReportDTO report, BindingResult bindingResult, Model model) {


        if (bindingResult.hasErrors()) {
            return "add-new-report";
        }
        userAddingReportService.addNewReport(report);

        return "redirect:/new-report?success";

    }
}
