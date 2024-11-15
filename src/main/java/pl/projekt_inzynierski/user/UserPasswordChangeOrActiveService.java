package pl.projekt_inzynierski.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pl.projekt_inzynierski.mailing.MailService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserPasswordChangeOrActiveService {

    private final VeryficationTokenRepository veryficationTokenRepository;
    private final MailService mailService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    @Autowired
    public UserPasswordChangeOrActiveService(VeryficationTokenRepository veryficationTokenRepository, MailService mailService
    , UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.veryficationTokenRepository = veryficationTokenRepository;
        this.mailService = mailService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    private final int VERIFICATION_ACCOUNT_EXPIRATION_TIME = 60 * 24 * 7; // 7 dni
    private final int PASSWORD_RESET_EXPIRATION_TIME = 60; //60 min

    public void NewVerification(User user) {
        VeryficationToken veryficationToken = new VeryficationToken(UUID.randomUUID().toString(), VERIFICATION_ACCOUNT_EXPIRATION_TIME, user);
        veryficationTokenRepository.save(veryficationToken);
        String verifUrl = ServletUriComponentsBuilder.fromCurrentContextPath().path("user/verification/" + veryficationToken.getToken()).build().toUriString();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

        mailService.NewUserWelecomeMessage(user.getEmail(), user.getFirstName(), verifUrl, veryficationToken.getExpirationTime().format(formatter));

    }
    public void NewResetPassword(User user) {

    }
    public boolean UserMailExists(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        return user.isPresent();
    }
    public String validateToken(String token) {

        Optional<VeryficationToken> byToken = veryficationTokenRepository.findByToken(token);
        if (byToken.isPresent()) {
            if (!byToken.get().getUser().getIsActive()){
                if (byToken.get().getExpirationTime().isAfter(LocalDateTime.now()) ){
                    return "OK";
                }else {return "tokenExpired";}
            }else {return "alreadyAactive";}
        }
        return "error";
    }
    public void SetNewPassword(String token, String password) {
        Optional<VeryficationToken> byToken = veryficationTokenRepository.findByToken(token);
        User user = byToken.get().getUser();
        user.setPassword(passwordEncoder.encode(password));
        user.setActive(true);
        userRepository.save(user);

        byToken.get().setExpirationTime(LocalDateTime.now().minusMinutes(1));
        veryficationTokenRepository.save(byToken.get());
    }
}