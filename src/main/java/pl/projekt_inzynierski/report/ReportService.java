package pl.projekt_inzynierski.report;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.projekt_inzynierski.Dto.ChatQueueDto;
import pl.projekt_inzynierski.chat.ChatMessage;
import pl.projekt_inzynierski.mailing.ChatNotificationQueue;
import pl.projekt_inzynierski.user.User;
import pl.projekt_inzynierski.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class ReportService {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final ChatNotificationQueue chatNotificationQueue;

    public ReportService(UserRepository userRepository, ReportRepository reportRepository, ChatNotificationQueue chatNotificationQueue) {
        this.userRepository = userRepository;
        this.reportRepository = reportRepository;
        this.chatNotificationQueue = chatNotificationQueue;
    }


    List<Report> getReportsWithoutEmployee() {
        return reportRepository.findAllByAssignedUserIsNull().stream()
                .filter(report -> report.getStatus() != ReportStatus.COMPLETED)
                .collect(Collectors.toList());
    }

    @Transactional
    public void addNewMessage(ChatMessage message) {
        Report report = reportRepository.findById(message.getReportId()).orElseThrow();
        report.getMessages().add(message);

        ChatQueueDto newChatDto = new ChatQueueDto();
        newChatDto.setReportId(report.getId());
        newChatDto.setMessage(message.getValue());
        newChatDto.setRemindTime(LocalDateTime.now().plusMinutes(2));
        newChatDto.setSender(message.getUser());
        newChatDto.setSentTime(message.getTimestamp());
        if (report.getReportingUser().getEmail().equals(message.getUser())) {
            if (report.getAssignedUser() == null) {
                List<String> adminsEmail = userRepository.findAllUserByRolesName("ADMINISTRATOR").stream().map(User::getEmail).toList();
                newChatDto.setReceiver(String.join(", ", adminsEmail));
            }else {
                newChatDto.setReceiver(report.getAssignedUser().getEmail());
            }

        }else {
            newChatDto.setReceiver(report.getReportingUser().getEmail());
        }

        chatNotificationQueue.addChatQueueNotification(newChatDto);


    }

    List<Report> getAllReports() {
        return StreamSupport.stream(reportRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    List<Report> getAllReportsByAssignedEmployeeEmail(String email) {
        return reportRepository.findAllByAssignedUser_Email(email);
    }

    List<Report> getAllReportsByReportingUserEmail(String email) {
        return reportRepository.findAllByReportingUser_Email(email);
    }

    @Transactional
    public void assignEmployeeToReport(Long reportId, Long employeeId) {
        Report report = reportRepository.findById(reportId).orElseThrow(() -> new IllegalArgumentException("Report not found"));
        User employee = userRepository.findById(employeeId).orElseThrow(() -> new IllegalArgumentException("Employee not found"));

        if (!employee.getRoles().stream().anyMatch(role -> role.getName().equals("EMPLOYEE"))) {
            throw new IllegalArgumentException("User is not an employee");
        }

        report.setAssignedUser(employee);
        reportRepository.save(report);
    }
    @Transactional
    public void saveNewReport(Report report) {
        reportRepository.save(report);
    }

    public ChatMessage getLastMessage(Report report) {
        List<ChatMessage> messages = report.getMessages();
        return messages.get(messages.size() - 1);
    }

}
