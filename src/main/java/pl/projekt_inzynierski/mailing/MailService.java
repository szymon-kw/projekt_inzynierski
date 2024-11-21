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
import pl.projekt_inzynierski.Dto.ReportDTO;
import pl.projekt_inzynierski.Dto.ToSendReminderDTO;
import pl.projekt_inzynierski.report.RemainingTime;
import pl.projekt_inzynierski.report.Report;
import pl.projekt_inzynierski.report.ReportRepository;
import pl.projekt_inzynierski.report.ReportStatus;
import pl.projekt_inzynierski.user.User;
import pl.projekt_inzynierski.user.UserRepository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class MailService {

    private final JavaMailSender mailSender;
    private final ISpringTemplateEngine templateEngine;
    private final EmailQueue emailQueue;
    private final UserRepository userRepository;
    private final ReportRepository reportRepository;

    @Autowired
    public MailService(JavaMailSender mailSender, ISpringTemplateEngine templateEngine
            , EmailQueue emailQueue, UserRepository userRepository, ReportRepository reportRepository) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
        this.emailQueue = emailQueue;
        this.userRepository = userRepository;
        this.reportRepository = reportRepository;
    }

    @Value("$spring.mail.username")
    private String fromEmailId;
    Logger logger = LoggerFactory.getLogger(MailService.class);

    public void NewUserWelcomeMessage(String to, String UserName, String baseURL, String ExpirationDate) {
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
            ctx.setVariable("ExpirationDate", ExpirationDate);

            String httpBody = templateEngine.process("mail-templates/new_user.html", ctx);
            helper.setText(httpBody, true);

            emailQueue.addEmailToQueue(helper);
            logger.info("New Mail to: {} Queued | Category: New User", to);

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

    public void NewReportCreatedNotificationMessage(String to, String userName, String reportTitle, String addedTime){

        try{
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

            helper.setFrom(fromEmailId);
            helper.setTo(to);
            helper.setSubject("Przyjęliśmy twoje zgłoszenie");
            Context ctx = new Context();
            ctx.setVariable("UserName", userName);
            ctx.setVariable("ReportTitle", reportTitle);
            ctx.setVariable("AddDate", addedTime);
            String httpBody = templateEngine.process("mail-templates/new_report_notif_to_user.html", ctx);
            helper.setText(httpBody, true);
            mimeMessage.setDescription(to + " | Category: New Created Report Notification");
            emailQueue.addEmailToQueue(helper);
            logger.info("New Mail to {} Queued | Category: New Created Report Notification", to);

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
            logger.info("Assign Notification to: {} Queued | Category: Assign Notification", to);

        }catch (MessagingException e){
            logger.error("Can't add e-mail to queue: " + e.getMessage());
        }
    }
    public void ForgotPasswordMessage(String to, String name, String tokenURI, String expirationTime){

        try {

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
            helper.setFrom(fromEmailId);
            helper.setTo(to);
            helper.setSubject("Prośba o zmianę hasła");
            mimeMessage.setDescription(to + " | Category: Change Password");
            //parametry do przekazania w modelu
            Context ctx = new Context();
            ctx.setVariable("UserName", name);
            ctx.setVariable("uriLink", tokenURI);
            ctx.setVariable("ExpirationDate", expirationTime);

            String httpBody = templateEngine.process("mail-templates/change_password.html", ctx);
            helper.setText(httpBody, true);

            emailQueue.addEmailToQueue(helper);
            logger.info("New Mail to: {} Queued | Category: Change Password", to);

        }catch (MessagingException e){
            logger.error("Can't add e-mail to queue: " + e.getMessage());
        }

    }

    public void ReminderMessage(ToSendReminderDTO toSend){

        try {

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
            helper.setFrom(fromEmailId);
            helper.setTo(toSend.getTo());
            helper.setSubject(toSend.getTypesOfReminderMail().subject + "!");
            mimeMessage.setDescription(toSend.getTo() + " | Category: New Reminder Message");
            //parametry do przekazania w modelu
            Context ctx = new Context();
            ctx.setVariable("Reports", toSend.getReports());
            ctx.setVariable("Title", toSend.getTypesOfReminderMail().subject);
            ctx.setVariable("TitleFragment", toSend.getTypesOfReminderMail().titleFragment);

            String httpBody = templateEngine.process("mail-templates/new_remind.html", ctx);
            helper.setText(httpBody, true);

            emailQueue.addEmailToQueue(helper);
            logger.info("New Mail to: {} Queued | Category: New Reminder Message", toSend.getTo());

        }catch (MessagingException e){
            logger.error("Can't add e-mail to queue: " + e.getMessage());
        }

    }

    //@Scheduled(cron = "0 0 8 * * *")
    public void noCloseReportReminder(){

        List<Report> AllReports = reportRepository.findAllByStatusNot(ReportStatus.COMPLETED);
        if (!AllReports.isEmpty()) {

            List<String> adminsEmail = userRepository.findAllUserByRolesName("ADMINISTRATOR").stream().map(User::getEmail).toList();

            List<Report> reportsForEmployee = AllReports.stream()
                    .filter(report -> report.getAssignedUser() != null)
                    .filter(report -> !adminsEmail.contains(report.getAssignedUser().getEmail()))
                    .toList();

            Map<String, List<ReportDTO>> groupedReportsForEmployee = reportsForEmployee.stream()
                    .collect(Collectors.groupingBy(
                            report -> report.getAssignedUser().getEmail(),
                            Collectors.mapping(this::covertToReportDTO, Collectors.toList())
                    ));

            List<ToSendReminderDTO> ToSendEmployee = groupedReportsForEmployee.entrySet()
                    .stream()
                    .map(entry -> {
                        ToSendReminderDTO dto = new ToSendReminderDTO();
                        dto.setTo(entry.getKey());
                        dto.setTypesOfReminderMail(TypesOfReminderMail.EMP_ALL); //do zmiany
                        dto.setReports(entry.getValue());
                        return dto;
                    })
                    .toList();

            ToSendReminderDTO ToSendAdmin = new ToSendReminderDTO();
            ToSendAdmin.setTo(String.join(", ", adminsEmail));
            ToSendAdmin.setTypesOfReminderMail(TypesOfReminderMail.ADM_All); //do zmiany
            ToSendAdmin.setReports(AllReports.stream().map(this::covertToReportDTO).toList());

            List<ToSendReminderDTO> FinalToSendFrame = new ArrayList<>();

            FinalToSendFrame.add(ToSendAdmin);
            FinalToSendFrame.addAll(ToSendEmployee);

            for (ToSendReminderDTO ToSendFrame : FinalToSendFrame) {
                ReminderMessage(ToSendFrame);
            }
        }

    }


    //@Scheduled (fixedRate = 1, timeUnit = TimeUnit.HOURS) //every hour
    public void shortTimeToCloseReportsReminder(){

        List<ToSendReminderDTO> ToSendFrames = prepareSendingFrameForEndingReports();

        for (ToSendReminderDTO ToSendFrame : ToSendFrames) {
            ReminderMessage(ToSendFrame);
        }

    }


    @Scheduled (fixedRate = 10000) //co 10 sec
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
                        logger.error("Can't get Mail info: {}", e.getMessage());
                    }

                    logger.info("Send e-mail to: {}", descripion);
                } catch (MailException e) {
                    logger.error("Can't send e-mail: {}", e.getMessage());
                }

            }
        }
    }

    // # Private Functions #
    private ReportDTO covertToReportDTO(Report report){
        ReportDTO reportDTO = new ReportDTO();
        reportDTO.setReportTitle(report.getTitle());
        reportDTO.setReportingUserName(report.getReportingUser().getFirstName() + " " + report.getReportingUser().getLastName());
        reportDTO.setReportingCompanyName(report.getReportingUser().getCompany().getName());
        reportDTO.setTimeToLeft(GetTimeToLeft(report));
        return reportDTO;
    }
    private String GetTimeToLeft(Report report){

        RemainingTime remainingTime = report.getRemainingTime(false);

        if (remainingTime.isExpired()){return "Cza minął";}


        StringBuilder result = new StringBuilder();
        if (remainingTime.getDays() > 0) {
            result.append(remainingTime.getDays()).append(" Dni ");
        }
        if (remainingTime.getHours() > 0) {
            result.append(remainingTime.getHours()).append(" Godz. ");
        }
        if (remainingTime.getMinutes() > 0) {
            result.append(remainingTime.getMinutes()).append(" Min.");
        }
        return result.toString().trim();
    }

    private List<ToSendReminderDTO> prepareSendingFrameForEndingReports(){

        List<String> adminsEmail = userRepository.findAllUserByRolesName("ADMINISTRATOR").stream().map(User::getEmail).toList();
        List<Report> AllReports = reportRepository.findAllByStatusNot(ReportStatus.COMPLETED);

        List<Report> reportSetForAdminPending = GetWantedReport(true, true, adminsEmail, AllReports);
        List<Report> reportSetForEmployeePending = GetWantedReport(false, true, adminsEmail, AllReports);
        List<Report> reportSetForAdminReview = GetWantedReport(true, false, adminsEmail, AllReports);
        List<Report> reportSetForEmployeeReview = GetWantedReport(false, false, adminsEmail, AllReports);


        Map<String, List<ReportDTO>> groupedReportsForPending = reportSetForEmployeePending.stream()
                .collect(Collectors.groupingBy(
                        report -> report.getAssignedUser().getEmail(),
                        Collectors.mapping(this::covertToReportDTO, Collectors.toList())
                ));
        Map<String, List<ReportDTO>> groupedReportsForReview = reportSetForEmployeeReview.stream()
                .collect(Collectors.groupingBy(
                        report -> report.getAssignedUser().getEmail(),
                        Collectors.mapping(this::covertToReportDTO, Collectors.toList())
                ));

        List<ToSendReminderDTO> ToSendEmpPending = groupedReportsForPending.entrySet()
                .stream()
                .map(entry -> {
                    ToSendReminderDTO dto = new ToSendReminderDTO();
                    dto.setTo(entry.getKey());
                    dto.setTypesOfReminderMail(TypesOfReminderMail.EMP_REACTION);
                    dto.setReports(entry.getValue());
                    return dto;
                })
                .toList();

        List<ToSendReminderDTO> ToSendEmpRev = groupedReportsForReview.entrySet()
                .stream()
                .map(entry -> {
                    ToSendReminderDTO dto = new ToSendReminderDTO();
                    dto.setTo(entry.getKey());
                    dto.setTypesOfReminderMail(TypesOfReminderMail.EMP_ENDS);
                    dto.setReports(entry.getValue());
                    return dto;
                })
                .toList();


        ToSendReminderDTO ToSendAdmPending = new ToSendReminderDTO();
        ToSendAdmPending.setTo(String.join(", ", adminsEmail));
        ToSendAdmPending.setTypesOfReminderMail(TypesOfReminderMail.ADM_REACTION);
        ToSendAdmPending.setReports(reportSetForAdminPending.stream().map(this::covertToReportDTO).toList());

        ToSendReminderDTO ToSendAdmRev = new ToSendReminderDTO();
        ToSendAdmRev.setTo(String.join(", ", adminsEmail));
        ToSendAdmRev.setTypesOfReminderMail(TypesOfReminderMail.ADM_ENDS);
        ToSendAdmRev.setReports(reportSetForAdminReview.stream().map(this::covertToReportDTO).toList());

        List<ToSendReminderDTO> ToSend = new ArrayList<>();
        ToSend.addAll(ToSendEmpPending);
        ToSend.addAll(ToSendEmpRev);
        if (!ToSendAdmPending.getReports().isEmpty()) ToSend.add(ToSendAdmPending);
        if (!ToSendAdmRev.getReports().isEmpty()) ToSend.add(ToSendAdmRev);

        return ToSend;

    }
    private List<Report> GetWantedReport(boolean forAdmin, boolean ifPending, List<String> adminsEmail, List<Report> AllReports){
        //dla adminów przejdzie wszusko a dla emp nie
        return AllReports
                .stream()
                .filter(report -> forAdmin || report.getAssignedUser() != null) //dla adminów przejdzie wszusko a dla emp nie
                .filter(report -> forAdmin || !adminsEmail.contains(report.getAssignedUser().getEmail()))
                .filter(report -> isUrgent(report, ifPending))
                .filter(report -> filterByStatus(report, ifPending))
                .toList();
    }

    private boolean filterByStatus(Report report, boolean ifPending){
        if (ifPending){
            return report.getStatus() == ReportStatus.PENDING;
        }else {
            return report.getStatus() == ReportStatus.UNDER_REVIEW;
        }
    }

    private boolean isUrgent(Report report, boolean ifPending) {

        Duration timeLeft = Duration.between(LocalDateTime.now(), ifPending? report.getTimeToRespond() : report.getDueDate());
        return timeLeft.toMinutes() <= 90;

    }
}

