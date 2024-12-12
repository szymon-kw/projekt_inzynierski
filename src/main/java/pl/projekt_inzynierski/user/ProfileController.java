package pl.projekt_inzynierski.user;

import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import pl.projekt_inzynierski.Dto.PasswordsDTO;

@Controller
public class ProfileController {

    private final UserManagementService userManagementService;

    public ProfileController(UserManagementService userManagementService) {
        this.userManagementService = userManagementService;
    }

    @GetMapping("/profile")
    public String getProfile(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        User user = userManagementService.findByEmail(currentUsername);
        if (user == null) {
            throw new IllegalArgumentException("User not found with email: " + currentUsername);
        }

        model.addAttribute("user", user);
        return "profile";
    }


    @PostMapping("/profile")
    public String updateProfile(
            String email,
            @Valid PasswordsDTO passwordsDTO,
            BindingResult bindingResult,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("user", userManagementService.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName()));
            model.addAttribute("error", "Hasło jest nieprawidłowe lub dane są niekompletne.");
            return "profile";
        }

        if (!passwordsDTO.getPassword().equals(passwordsDTO.getConfirmPassword())) {
            model.addAttribute("user", userManagementService.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName()));
            model.addAttribute("error", "Hasła muszą być takie same!");
            return "profile";
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        User user = userManagementService.findByEmail(currentUsername);

        if (user == null) {
            throw new IllegalArgumentException("User not found with email: " + currentUsername);
        }


        userManagementService.updateUserProfile(user.getId(), email, passwordsDTO.getPassword());


        if (!currentUsername.equals(email)) {
            userManagementService.updateAuthentication(email);
        }

        return "redirect:/profile?success";
    }


}
