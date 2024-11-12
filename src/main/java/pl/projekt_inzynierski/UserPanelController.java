package pl.projekt_inzynierski;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pl.projekt_inzynierski.mailing.MailService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Controller
public class UserPanelController {

    private final MailService mailService;

    @Autowired
    public UserPanelController(MailService mailService) {
        this.mailService = mailService;
    }

    @GetMapping("/home")
    public String homePanel() {
        return "user_panel";
    }

    @GetMapping("/testmail")
    @ResponseBody
    public String testmail() {

        mailService.NewUserWelecomeMessage("noreply.appproject@gmail.com", "Dawid"
                , ServletUriComponentsBuilder.fromCurrentContextPath().path("home").build().toUriString()
                , "ByleJakieHaslo");

        mailService.NewReportNotificationToAdmins("Niedziałająca strona web", "Michał",
                "Example Company SA", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")));
        return "wysłano wiadomośc email";
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
