package pl.projekt_inzynierski.mailing;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.ISpringTemplateEngine;
import pl.projekt_inzynierski.user.User;
import pl.projekt_inzynierski.user.UserRepository;

@Service
public class MailService {

    private final JavaMailSender mailSender;
    private final ISpringTemplateEngine templateEngine;
    private final EmailQueue emailQueue;
    private final UserRepository userRepository;


    @Autowired
    public MailService(JavaMailSender mailSender, ISpringTemplateEngine templateEngine
            , EmailQueue emailQueue, UserRepository userRepository) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
        this.emailQueue = emailQueue;
        this.userRepository = userRepository;
    }

    @Value("$spring.mail.username")
    private String fromEmailId;
    Logger logger = LoggerFactory.getLogger(MailService.class);



    public void NewUserWelecomeMessage(String to, String UserName, String baseURL, String UserPassword) {
        final String title = "Witaj";
        try {

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
            helper.setFrom(fromEmailId);
            helper.setTo(to);
            helper.setSubject(title);
            mimeMessage.setDescription(to + " | Category: New User");
            //parametry do przekazania w modelu
            Context ctx = new Context();
            ctx.setVariable("UserName", UserName);
            ctx.setVariable("pageTitle", title);
            ctx.setVariable("uriLink", baseURL);
            ctx.setVariable("UserEmail", to);
            ctx.setVariable("UserPassword", UserPassword);

            String httpBody = templateEngine.process("mail-templates/new_user.html", ctx);
            helper.setText(httpBody, true);

            emailQueue.addEmailToQueue(helper);
            logger.info("New Mail to: " + to + " Queued | Category: New User");

        }catch (MessagingException e){
            logger.error("Can't add e-mail to queue: " + e.getMessage());
        }

    }
    public void NewReportNotificationToAdmins(String ReportTitle, String ReportingUserName, String ReportingCompanyName, String addDate){
        //Pobranie wszyskich adminów

        final String[] AdminEmails = userRepository.findAllUserByRolesName("ADMINISTRATOR")
                .stream().map(User::getEmail).toArray(String[]::new);
        final String title = "Nowe Zgłoszenie od " + ReportingUserName;


        try{
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

            helper.setFrom(fromEmailId);
            helper.setTo(AdminEmails);
            helper.setSubject(title);
            Context ctx = new Context();
            ctx.setVariable("ReportTitle", ReportTitle);
            ctx.setVariable("pageTitle", title);
            ctx.setVariable("CompanyName", ReportingCompanyName);
            ctx.setVariable("CreatorUserName", ReportingUserName);
            ctx.setVariable("AddDate", addDate);
            String httpBody = templateEngine.process("mail-templates/new_report_notif_to_admin.html", ctx);
            helper.setText(httpBody, true);
            mimeMessage.setDescription("Admins | Category: New Report");
            emailQueue.addEmailToQueue(helper);
            logger.info("New Mail to Admins Queued | Category: New Report");

        } catch (MessagingException e) {
            logger.error("Can't add e-mail to queue: " + e.getMessage());
        }
    }

    public void  AssignNotificationMessage(String to, String ReportTitle, String ReportCompany, String ReportDate, String ReportingUserName){
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

            helper.setFrom(fromEmailId);
            helper.setTo(to);
            helper.setSubject("Przypisano do ciebie zgłoszenie");
            mimeMessage.setDescription(to + " | Category: Assign Notification");

            Context ctx = new Context();
            ctx.setVariable("ReportTitle", ReportTitle);
            ctx.setVariable("ReportCompany", ReportCompany);
            ctx.setVariable("ReportDate", ReportDate);
            ctx.setVariable("ReportUserName", ReportingUserName);

            String httpBody = templateEngine.process("mail-templates/assign_notification.html", ctx);
            helper.setText(httpBody, true);
            emailQueue.addEmailToQueue(helper);
            logger.info("Assign Notification to: " + to +" Queued | Category: Assign Notification");

        }catch (MessagingException e){
            logger.error("Can't add e-mail to queue: " + e.getMessage());
        }
    }

    @Scheduled (fixedRate = 10000) //co 10 sekund
    @Async
    public void proccessEmailQueue() {
        while (!emailQueue.isEmpty()) {
            MimeMessageHelper mimeMessageHelper = emailQueue.getNextEmail();

            if (mimeMessageHelper != null) {
                try{
                    String descripion = "";
                    mailSender.send(mimeMessageHelper.getMimeMessage());

                    try {
                        descripion = mimeMessageHelper.getMimeMessage().getDescription();
                    }catch (MessagingException e){
                        logger.error("Can't get Mail info: " +e.getMessage());
                    }

                    logger.info("Send e-mail to: " + descripion);
                } catch (MailException e) {
                    logger.error("Can't send e-mail: " + e.getMessage());
                }

            }
        }
    }

}

