package pl.projekt_inzynierski;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.projekt_inzynierski.company.CompanyRepository;
import pl.projekt_inzynierski.user.User;
import pl.projekt_inzynierski.Dto.UserDto;
import pl.projekt_inzynierski.user.UserManagementService;
import pl.projekt_inzynierski.user.UserRoleRepository;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class UserManagementController {

    private final UserManagementService userManagementService;
    private final CompanyRepository companyRepository;
    private final UserRoleRepository userRoleRepository;

    @Autowired
    public UserManagementController(UserManagementService userManagementService,
                                    CompanyRepository companyRepository,
                                    UserRoleRepository userRoleRepository) {
        this.userManagementService = userManagementService;
        this.companyRepository = companyRepository;
        this.userRoleRepository = userRoleRepository;
    }

    @GetMapping("/manage_users")
    public String manageUsers(Model model) {
        List<User> users = userManagementService.findAllUsers();
        model.addAttribute("users", users);

        // Dodaj listy firm i ról do modelu dla formularza
        model.addAttribute("companies", companyRepository.findAll());
        model.addAttribute("roles", userRoleRepository.findAll());

        return "manage_users";
    }

    @PostMapping("/add_user")
    public String addUser(UserDto user) {
        userManagementService.saveUser(user);
        return "redirect:/admin/manage_users";
    }

    @PostMapping("/delete_user/{id}")
    public String deleteUser(@PathVariable Long id) {
        userManagementService.deleteUser(id);
        return "redirect:/admin/manage_users";
    }
    @GetMapping("/edit_user/{id}")
    public String editUserForm(@PathVariable Long id, Model model) {
        User user = userManagementService.findUserById(id);
        model.addAttribute("user", user);


        model.addAttribute("companies", companyRepository.findAll());
        model.addAttribute("roles", userRoleRepository.findAll());

        return "edit_user";
    }

    @PostMapping("/edit_user/{id}")
    public String editUser(@PathVariable Long id, User updatedUser, Long companyId, Long roleId, String newPassword) {
        userManagementService.updateUser(id, updatedUser, companyId, roleId, newPassword);
        return "redirect:/admin/manage_users";
    }

}
