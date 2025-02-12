package pl.projekt_inzynierski.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pl.projekt_inzynierski.Dto.NewReportDTO;
import pl.projekt_inzynierski.attachment.Attachment;
import pl.projekt_inzynierski.file.FileService;
import pl.projekt_inzynierski.mailing.MailService;
import pl.projekt_inzynierski.report.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserAddingReportService {

    private final FileService fileService;
    private final ReportService reportService;
    private final UserRepository userRepository;
    private final MailService mailService;
    private final ReportCategoryService reportCategoryService;


    @Autowired
    public UserAddingReportService(FileService fileService, ReportService reportService
            , UserRepository userRepository, MailService mailService, ReportCategoryService reportCategoryService) {
        this.fileService = fileService;
        this.reportService = reportService;
        this.userRepository = userRepository;
        this.mailService = mailService;
        this.reportCategoryService = reportCategoryService;
    }

    public void addNewReport(NewReportDTO newReportDTO) {

        Report report = new Report();

        ReportCategory category = reportCategoryService.getReportCategoryById(newReportDTO.getCategoryId());

        report.setTitle(newReportDTO.getTitle());
        report.setDescription(newReportDTO.getDescription());

        //pobranie użytkownika
        User user = userRepository.findByEmail(SecurityContextHolder
                .getContext().getAuthentication().getName())
                .orElseThrow(() ->new UsernameNotFoundException("User not found: User-Adding-Report-Service"));

        report.setDateAdded(LocalDateTime.now());
        report.setDueDate(LocalDateTime.now().plusDays(user.getCompany().getTimeToResolve()));
        report.setTimeToRespond(LocalDateTime.now().plusHours(user.getCompany().getTimeToFirsRespond()));
        report.setCategory(category);
        report.setStatus(ReportStatus.PENDING);
        report.setReportingUser(user);

        //obsługa plików
        if (!newReportDTO.getFile().isEmpty()) {
            List<Attachment> attachments = new ArrayList<>();

            for (MultipartFile file : newReportDTO.getFile()) {
                String fileName = file.getOriginalFilename();
                Path filePath = Paths.get("uploads/" + fileName);
                int fileIndex = 1;
                while (fileService.filenameAlreadyExists(filePath.getFileName().toString())) {
                    filePath = fileService.createPathWithUniqueFilename(fileName, fileIndex,"uploads/");
                    fileIndex ++;
                }
                try {
                    Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                    Attachment attachment = new Attachment();
                    attachment.setFilePath("/uploads/" + filePath.getFileName().toString());
                    attachment.setTimestamp(LocalDateTime.now());
                    attachment.setAddingUser(user.getEmail());
                    attachment.setFileName(file.getOriginalFilename());
                    attachment.setFileSize(file.getSize());
                    report.getAttachments().add(attachment);

                }catch (IOException e) {
                    System.out.println(e.getMessage());
                }

            }
        }

        reportService.saveNewReport(report);

        //powiadomienie mailowe

        mailService.NewReportNotificationToAdmins(report.getTitle(), user.getFirstName(), user.getCompany().getName(), report.getDateAdded().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")));
        mailService.NewReportCreatedNotificationMessage(user.getEmail(), user.getFirstName(), report.getTitle(), LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
    }

}
