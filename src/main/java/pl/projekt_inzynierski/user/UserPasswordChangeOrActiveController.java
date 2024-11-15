package pl.projekt_inzynierski.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/user")
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
        String Validation = userPasswordChangeOrActiveService.validateToken(token);
        if (!Validation.equals("OK")) {
              return "redirect:/user/verificationError?" + Validation;
        }
        model.addAttribute("verification", true);
        model.addAttribute("token", token);
        return "user_new_password";
    }
    @PostMapping("/verification/{token}")
    public String AssignPassword(@PathVariable(required = true) String token, @RequestParam String password, @RequestParam String password_repit) {
        String Validation = userPasswordChangeOrActiveService.validateToken(token);
        if (!Validation.equals("OK")) {
            return "redirect:/user/verificationError?" + Validation;
        } else if (!password.equals(password_repit)) {
            return "redirect:/user/verification/" + token + "?diffrentPassword";
        }

        userPasswordChangeOrActiveService.SetNewPassword(token, password);
        return "redirect:/user/verification?success";
    }
}
