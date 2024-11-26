package pl.projekt_inzynierski;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import pl.projekt_inzynierski.mailing.MailService;
import pl.projekt_inzynierski.report.RemainingTime;
import pl.projekt_inzynierski.report.Report;
import pl.projekt_inzynierski.report.ReportRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Controller
public class ForTestsContoler {

    private final ReportRepository reportRepository;
    private final MailService mailService;

    @Autowired
    public ForTestsContoler(ReportRepository reportRepository, MailService mailService) {
        this.reportRepository = reportRepository;
        this.mailService = mailService;
    }

    @GetMapping("/testmail")
    @ResponseBody
    public String testmail() {

        /*mailService.NewUserWelecomeMessage("noreply.appproject@gmail.com", "Dawid"
                , ServletUriComponentsBuilder.fromCurrentContextPath().path("home").build().toUriString()
                , "ByleJakieHaslo");*/
        mailService.noCloseReportReminder();
        mailService.NewReportNotificationToAdmins("Niedziałająca strona web", "Michał",
                "Example Company SA", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")));
        return "wysłano wiadomośc email";


    }
    @GetMapping("/report-test")
    @ResponseBody
    public String reportTest() {

        Report report = reportRepository.findById(2L);
        RemainingTime remainingTime = report.getRemainingTime(true);
        RemainingTime remainingTime2 = report.getRemainingTime(false);

        return  "Test complete";
    }


}
