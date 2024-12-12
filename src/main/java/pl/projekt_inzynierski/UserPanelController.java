package pl.projekt_inzynierski;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import pl.projekt_inzynierski.user.User;
import pl.projekt_inzynierski.user.UserRepository;

@Controller
public class UserPanelController {

    private final UserRepository userRepository;

    @Autowired
    public UserPanelController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/home")
    public String homePanel(Authentication authentication, Model model) {
        User user = userRepository.findByEmail(authentication.getName()).orElseThrow(() -> new UsernameNotFoundException(authentication.getName()));
        model.addAttribute("UserName", user.getFirstName());
        return "user_panel";
    }

}
