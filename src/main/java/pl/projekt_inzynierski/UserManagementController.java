package pl.projekt_inzynierski;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
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
        model.addAttribute("companies", companyRepository.findAll());
        model.addAttribute("roles", userRoleRepository.findAll());

        return "manage_users";
    }

    @PostMapping("/add_user")
    public String addUser(UserDto user, Model model) {
        try {
            userManagementService.saveUser(user);
            model.addAttribute("successMessage", "Użytkownik został pomyślnie dodany.");
            model.addAttribute("users", userManagementService.findAllUsers());
            model.addAttribute("companies", companyRepository.findAll());
            model.addAttribute("roles", userRoleRepository.findAll());
            return "manage_users";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("users", userManagementService.findAllUsers());
            model.addAttribute("companies", companyRepository.findAll());
            model.addAttribute("roles", userRoleRepository.findAll());
            return "manage_users";
        }

    }

    @PostMapping("/delete_user/{id}")
    public String deleteUser(@PathVariable Long id, Model model) {
        try {
            userManagementService.deleteUser(id);
            model.addAttribute("successMessage", "Użytkownik został pomyślnie usunięty.");
        } catch (Exception e) {
            model.addAttribute("error", "Wystąpił problem podczas usuwania użytkownika.");
        }

        model.addAttribute("users", userManagementService.findAllUsers());
        model.addAttribute("companies", companyRepository.findAll());
        model.addAttribute("roles", userRoleRepository.findAll());

        return "manage_users";
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
    public String editUser(@PathVariable Long id, User updatedUser, Long companyId, Long roleId, String newPassword, Model model) {
        try {
            userManagementService.updateUser(id, updatedUser, companyId, roleId, newPassword);
            model.addAttribute("successMessage", "Dane użytkownika zostały pomyślnie zaktualizowane.");
            model.addAttribute("users", userManagementService.findAllUsers());
            model.addAttribute("companies", companyRepository.findAll());
            model.addAttribute("roles", userRoleRepository.findAll());
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            userManagementService.updateAuthentication(updatedUser.getEmail());
            return "manage_users";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("users", userManagementService.findAllUsers());
            model.addAttribute("companies", companyRepository.findAll());
            model.addAttribute("roles", userRoleRepository.findAll());
            return "manage_users";
        }
    }
}
