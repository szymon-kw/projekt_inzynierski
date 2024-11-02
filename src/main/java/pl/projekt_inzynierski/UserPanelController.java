package pl.projekt_inzynierski;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserPanelController {

    @GetMapping("/home")
    public String homePanel() {
        return "user_panel";
    }

//    @GetMapping("/user_panel")
//    public String userPanel() {
//        return "user_panel";
//    }
//
//    @GetMapping("/employee_panel")
//    public String employeePanel() {
//        return "employee_panel";
//    }
}
