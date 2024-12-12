package pl.projekt_inzynierski;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {


    @GetMapping("/")
    public String rootRedirect() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login() {
        return "login-page";
    }

    @GetMapping("/default")
    public String defaultAfterLogin() {
        return "redirect:/home";
    }
}
