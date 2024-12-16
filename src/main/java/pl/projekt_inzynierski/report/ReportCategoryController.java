package pl.projekt_inzynierski.report;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/ReportCategory")
public class ReportCategoryController {

    private final ReportCategoryService reportCategoryService;

    @Autowired
    public ReportCategoryController(ReportCategoryService reportCategoryService) {
        this.reportCategoryService = reportCategoryService;
    }

    @RequestMapping("")
    public String menageReportCategory(Model model) {

        Iterable<Report_Category> categories = reportCategoryService.getAllReportCategories();
        model.addAttribute("categories", categories);

        return "menage-report-category";
    }

}
