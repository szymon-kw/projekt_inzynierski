package pl.projekt_inzynierski;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class ProjektInzynierskiApplication {

    public static void main(String[] args) {
       SpringApplication.run(ProjektInzynierskiApplication.class, args);

    }

}
