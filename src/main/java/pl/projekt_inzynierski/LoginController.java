package pl.projekt_inzynierski;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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
//    public String defaultAfterLogin(Authentication authentication) {
//        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMINISTRATOR"))) {
//            return "redirect:/administrator_panel";
//        } else if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_USER"))) {
//            return "redirect:/user_panel";
//        } else if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_EMPLOYEE"))) {
//            return "redirect:/employee_panel";
//        }

        return "redirect:/home";
    }
}
