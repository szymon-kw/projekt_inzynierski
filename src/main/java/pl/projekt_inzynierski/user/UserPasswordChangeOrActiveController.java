package pl.projekt_inzynierski.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/account")
public class UserPasswordChangeOrActiveController {

    private final UserPasswordChangeOrActiveService userPasswordChangeOrActiveService;

    @Autowired
    public UserPasswordChangeOrActiveController(UserPasswordChangeOrActiveService userPasswordChangeOrActiveService) {
        this.userPasswordChangeOrActiveService = userPasswordChangeOrActiveService;

    }
    @GetMapping("/verification")
    public String verificationStatus() {
        return "verification-error-page";
    }
    @GetMapping("/verificationError")
    public String verificationError() {
        return "verification-error-page";
    }

    @GetMapping("/verification/{token}")
    public String VerifiAccount(@PathVariable(required = true) String token, Model model) {
        String Validation = userPasswordChangeOrActiveService.validateToken(token, true);
        if (!Validation.equals("OK")) {
              return "redirect:/account/verificationError?" + Validation;
        }
        model.addAttribute("verification", true);
        model.addAttribute("token", token);
        model.addAttribute("formAction", "/account/verification/" + token);
        return "user_new_password";
    }
    @PostMapping("/verification/{token}")
    public String AssignPassword(@PathVariable(required = true) String token, @RequestParam String password, @RequestParam String password_repit) {
        String Validation = userPasswordChangeOrActiveService.validateToken(token, true);
        if (!Validation.equals("OK")) {
            return "redirect:/account/verificationError?" + Validation;
        } else if (!password.equals(password_repit)) {
            return "redirect:/account/verification/" + token + "?diffrentPassword";
        }

        userPasswordChangeOrActiveService.SetNewPassword(token, password);
        return "redirect:/account/verification?success";
    }

    @GetMapping("/forgot-password")
    public String forgotPassword() {
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String forgotPasswordEmail(@RequestParam String email) {
        if (userPasswordChangeOrActiveService.NewResetPasswordMail(email)){
            return "redirect:/account/forgot-password?ok";
        }
        return "redirect:/account/forgot-password?wrong";
    }
    @GetMapping("/forgot-password/{token}")
    public String changePassword(@PathVariable(required = true) String token, Model model) {
        String Validation = userPasswordChangeOrActiveService.validateToken(token, false);
        if (!Validation.equals("OK")) {
            return "redirect:/account/verificationError?" + Validation;
        }
        model.addAttribute("verification", false);
        model.addAttribute("token", token);
        model.addAttribute("formAction", "/account/forgot-password/" + token);
        return "user_new_password";
    }
    @PostMapping("/forgot-password/{token}")
    public String changePasswordWithValues(@PathVariable(required = true) String token, @RequestParam String password, @RequestParam String password_repit) {
        String Validation = userPasswordChangeOrActiveService.validateToken(token, false);
        if (!Validation.equals("OK")) {
            return "redirect:/account/verificationError?" + Validation;
        } else if (!password.equals(password_repit)) {
            return "redirect:/account/forgot-password/" + token + "?diffrentPassword";
        }
        userPasswordChangeOrActiveService.SetNewPassword(token, password);
        return "redirect:/account/verification?success";
    }

}
