package pl.projekt_inzynierski.user;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

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
    public String updateProfile(String email, String newPassword) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        User user = userManagementService.findByEmail(currentUsername);
        if (user == null) {
            throw new IllegalArgumentException("User not found with email: " + currentUsername);
        }

        userManagementService.updateUserProfile(user.getId(), email, newPassword);

        if (!currentUsername.equals(email)) {
            updateAuthentication(email);
        }

        return "redirect:/profile?success";
    }

    private void updateAuthentication(String newEmail) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Authentication newAuth = new UsernamePasswordAuthenticationToken(
                newEmail, authentication.getCredentials(), authentication.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(newAuth);
    }
}
