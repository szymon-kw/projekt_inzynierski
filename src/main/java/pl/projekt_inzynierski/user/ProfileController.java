package pl.projekt_inzynierski.user;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import pl.projekt_inzynierski.Dto.EmailUpdateDTO;
import pl.projekt_inzynierski.Dto.PasswordsDTO;
import java.util.Optional;

import java.util.Set;

@Controller
public class ProfileController {

    private final UserManagementService userManagementService;
    private final Validator validator;

    public ProfileController(UserManagementService userManagementService, Validator validator) {
        this.userManagementService = userManagementService;
        this.validator = validator;
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
        model.addAttribute("emailUpdateDTO", new EmailUpdateDTO());
        model.addAttribute("passwordsDTO", new PasswordsDTO());
        return "profile";
    }


    @PostMapping("/profile")
    public String updateProfile(
            String action,
            EmailUpdateDTO emailUpdateDTO,
            BindingResult emailBindingResult,
            PasswordsDTO passwordsDTO,
            BindingResult passwordBindingResult,
            String currentPassword,
            Model model
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        User user = userManagementService.findByEmail(currentUsername);

        if (user == null) {
            throw new IllegalArgumentException("User not found with email: " + currentUsername);
        }

        if ("updateEmail".equals(action)) {

            if (emailUpdateDTO.getEmail() == null || emailUpdateDTO.getEmail().isEmpty()) {
                model.addAttribute("user", user);
                model.addAttribute("error", "Dane email są nieprawidłowe.");
                return "profile";
            }

            if (emailUpdateDTO.getEmail().equalsIgnoreCase(user.getEmail())) {
                model.addAttribute("user", user);
                model.addAttribute("error", "Podany email jest już przypisany do konta.");
                return "profile";
            }

            Optional<User> existingUser = userManagementService.findUserByEmail(emailUpdateDTO.getEmail());
            if (existingUser.isPresent() && !existingUser.get().getId().equals(user.getId())) {
                model.addAttribute("user", user);
                model.addAttribute("error", "Podany email jest już używany przez innego użytkownika.");
                return "profile";
            }

            if (emailUpdateDTO.getCurrentPassword() == null || !userManagementService.isPasswordValid(user, emailUpdateDTO.getCurrentPassword())) {
                model.addAttribute("user", user);
                model.addAttribute("error", "Obecne hasło jest nieprawidłowe.");
                return "profile";
            }

            userManagementService.updateEmail(user.getId(), emailUpdateDTO.getEmail());
            userManagementService.updateAuthentication(emailUpdateDTO.getEmail());
            model.addAttribute("successMessage", "Email został zmieniony pomyślnie.");
            model.addAttribute("user", userManagementService.findByEmail(emailUpdateDTO.getEmail()));
            return "profile";
        }

        if ("updatePassword".equals(action)) {

            Set<ConstraintViolation<PasswordsDTO>> violations = validator.validate(passwordsDTO);
            if (!violations.isEmpty()) {
                model.addAttribute("user", user);
                model.addAttribute("error", "Hasło nie spełnia wymagań walidacji.");
                return "profile";
            }

            if (passwordsDTO.getPassword() == null || passwordsDTO.getConfirmPassword() == null
                    || !passwordsDTO.getPassword().equals(passwordsDTO.getConfirmPassword())) {
                model.addAttribute("user", user);
                model.addAttribute("error", "Nowe hasła muszą być identyczne i poprawne.");
                return "profile";
            }

            if (currentPassword == null || !userManagementService.isPasswordValid(user, currentPassword)) {
                model.addAttribute("user", user);
                model.addAttribute("error", "Obecne hasło jest nieprawidłowe.");
                return "profile";
            }

            userManagementService.updatePassword(user.getId(), passwordsDTO.getPassword());
            model.addAttribute("successMessage", "Hasło zostało zmienione pomyślnie.");
            model.addAttribute("user", user);
            return "profile";
        }

        model.addAttribute("user", user);
        return "profile";
    }


}
