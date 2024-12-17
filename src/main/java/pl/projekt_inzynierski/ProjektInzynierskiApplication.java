package pl.projekt_inzynierski;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.projekt_inzynierski.report.ReportController;
import pl.projekt_inzynierski.report.ReportService;
import pl.projekt_inzynierski.report.ReportStatus;

@SpringBootApplication
@EnableScheduling
public class ProjektInzynierskiApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(ProjektInzynierskiApplication.class, args);
    }

}
