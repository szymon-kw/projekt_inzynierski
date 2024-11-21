package pl.projekt_inzynierski.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pl.projekt_inzynierski.Dto.NewReportDTO;

@Controller
@RequestMapping("/new-report")
public class UserAddingReportController {

    private final UserAddingReportService userAddingReportService;

    @Autowired
    public UserAddingReportController(UserAddingReportService userAddingReportService) {
        this.userAddingReportService = userAddingReportService;
    }

    @GetMapping("")
    public String newReport(Model model) {
        model.addAttribute("report", new NewReportDTO());
        return "add-new-report";
    }
    @PostMapping(value = "/add", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public String addNewReport(NewReportDTO reportDTO) {

        if (!userAddingReportService.validReport(reportDTO)) {
            return "redirect:/new-report?invalid";
        }

        userAddingReportService.addNewReport(reportDTO);

        return "redirect:/new-report?success";

    }
}
