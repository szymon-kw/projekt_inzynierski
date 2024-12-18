package pl.projekt_inzynierski.report;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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

        Iterable<ReportCategory> categories = reportCategoryService.getAllReportCategories();
        model.addAttribute("categories", categories);
        model.addAttribute("categoryCount", reportCategoryService.getCountOfReportCategories());

        return "menage-report-category";
    }

    @PostMapping("/add")
    public String addReportCategory(@RequestParam String name) {

        if (reportCategoryService.isExists(name)) {
            return "redirect:/ReportCategory?exists";
        }
        ReportCategory reportCategory = new ReportCategory();
        reportCategory.setName(name);
        reportCategoryService.addNewReportCategory(reportCategory);

        return "redirect:/ReportCategory?success";

    }
    @GetMapping("/delete")
    public String delaeteCategory(@RequestParam Long id) {
        reportCategoryService.deleteReportCategory(id);
        return "redirect:/ReportCategory?delete";
    }
}
