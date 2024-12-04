package pl.projekt_inzynierski.company;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class CompanyManagementController {

    private final CompanyManagementService companyManagementService;

    @Autowired
    public CompanyManagementController(CompanyManagementService companyManagementService) {
        this.companyManagementService = companyManagementService;
    }

    @GetMapping("/manage_companies")
    public String manageCompanies(Model model) {
        List<Company> companies = companyManagementService.findAllCompanies();
        model.addAttribute("companies", companies);
        return "manage_companies";
    }

    @PostMapping("/add_company")
    public String addCompany(Company company, Model model) {
        try {
            companyManagementService.saveCompany(company);
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("companies", companyManagementService.findAllCompanies());
            return "manage_companies";
        }
        return "redirect:/admin/manage_companies";
    }


    @PostMapping("/edit_company/{id}")
    public String editCompany(@PathVariable Long id, Company updatedCompany) {
        companyManagementService.updateCompany(id, updatedCompany);
        return "redirect:/admin/manage_companies";
    }

    @PostMapping("/delete_company/{id}")
    public String deleteCompany(@PathVariable Long id) {
        companyManagementService.deleteCompany(id);
        return "redirect:/admin/manage_companies";
    }



}
