package pl.projekt_inzynierski;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserPanelController {


    @GetMapping("/home")
    public String homePanel() {
        return "user_panel";
    }

}
