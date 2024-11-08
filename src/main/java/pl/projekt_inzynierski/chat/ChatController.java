package pl.projekt_inzynierski.chat;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import pl.projekt_inzynierski.attachment.Attachment;
import pl.projekt_inzynierski.file.FileService;
import pl.projekt_inzynierski.report.Report;
import pl.projekt_inzynierski.report.ReportRepository;
import pl.projekt_inzynierski.report.ReportService;
import pl.projekt_inzynierski.user.User;
import pl.projekt_inzynierski.user.UserRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;

@Controller
public class ChatController {

    private final ChatMessageRepository chatMessageRepository;
    private final ReportRepository reportRepository;
    private final ReportService reportService;
    private final UserRepository userRepository;
    private final FileService fileService;

    public ChatController(ChatMessageRepository chatMessageRepository, ReportRepository reportRepository, ReportService reportService, UserRepository userRepository, FileService fileService) {
        this.chatMessageRepository = chatMessageRepository;
        this.reportRepository = reportRepository;
        this.reportService = reportService;
        this.userRepository = userRepository;
        this.fileService = fileService;
    }

    @GetMapping("/chat")
    public String showChatPage(@RequestParam("reportId") Long reportId, Model model) {
        Report report = reportRepository.findById(reportId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(username).orElseThrow();
        if(hasAccessToChat(user,report)) {
            model.addAttribute("report", report);
            model.addAttribute("reportId", reportId);
            model.addAttribute("recentMessages",report.getMessages());
            model.addAttribute("attachments", report.getAttachments());
            model.addAttribute("username", username);
            return "chat";
        }
        throw new ResponseStatusException(HttpStatus.FORBIDDEN);

    }

    private boolean hasAccessToChat(User user, Report report) {
        if(user.hasRole("ADMINISTRATOR") || report.getReportingUser().getEmail().contains(user.getEmail())) {
            return true;
        }
        return report.getAssignedUser() != null && report.getAssignedUser().getEmail().contains(user.getEmail());
    }

    @MessageMapping("/chat")
    @SendTo("/topic/messages")
    public ChatMessage get(@Payload ChatMessage chatMessage) {
        chatMessage.setTimestamp(LocalDateTime.now());
        chatMessageRepository.save(chatMessage);
        reportService.addNewMessage(chatMessage);
        return chatMessage;
    }

    @PostMapping("/chat/upload")
    @ResponseBody
    public ResponseEntity<String> uploadAttachment(@RequestParam("file") MultipartFile file, @RequestParam("reportId") Long reportId) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("plik nie może być pusty");
        }
        String fileName = file.getOriginalFilename();
        Path filePath = Paths.get("uploads/" + fileName);
        int fileIndex = 1;
        try {
            while(fileService.filenameAlreadyExists(filePath.getFileName().toString())) {
                filePath = fileService.createPathWithUniqueFilename(fileName, fileIndex, "uploads/");
                fileIndex++;
            }
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            Report report = reportRepository.findById(reportId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

            String username = SecurityContextHolder.getContext().getAuthentication().getName();

            Attachment attachment = new Attachment();
            attachment.setFilePath("/uploads/" + filePath.getFileName().toString());
            attachment.setTimestamp(LocalDateTime.now());
            attachment.setAddingUser(username);

            report.getAttachments().add(attachment);
            reportRepository.save(report);

            return ResponseEntity.ok("/uploads/" + filePath.getFileName().toString());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Błąd przy przesyłaniu pliku.");
        }
    }
}