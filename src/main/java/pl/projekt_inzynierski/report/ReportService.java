package pl.projekt_inzynierski.report;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.projekt_inzynierski.Dto.ChatQueueDto;
import pl.projekt_inzynierski.Dto.FinalListViewDto;
import pl.projekt_inzynierski.Dto.ListViewDto;
import pl.projekt_inzynierski.chat.ChatMessage;
import pl.projekt_inzynierski.chat.ChatMessageRepository;
import pl.projekt_inzynierski.mailing.ChatNotificationQueue;
import pl.projekt_inzynierski.mailing.MailService;
import pl.projekt_inzynierski.user.User;
import pl.projekt_inzynierski.user.UserRepository;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Comparator;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.springframework.data.domain.*;

@Service
public class ReportService {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final ChatNotificationQueue chatNotificationQueue;
    private final MailService mailService;
    private final ChatMessageRepository chatMessageRepository;


    public ReportService(UserRepository userRepository, ReportRepository reportRepository, ChatNotificationQueue chatNotificationQueue, MailService mailService,
                         ChatMessageRepository chatMessageRepository) {
        this.userRepository = userRepository;
        this.reportRepository = reportRepository;
        this.chatNotificationQueue = chatNotificationQueue;
        this.mailService = mailService;
        this.chatMessageRepository = chatMessageRepository;
    }


    List<Report> getReportsWithoutEmployee() {
        return reportRepository.findAllByAssignedUserIsNull().stream()
                .filter(report -> report.getStatus() != ReportStatus.COMPLETED)
                .collect(Collectors.toList());
    }

    @Transactional
    public void addNewMessage(ChatMessage message) {
        Report report = reportRepository.findById(message.getReportId()).orElseThrow();

        if (report.getStatus() != ReportStatus.COMPLETED) {
            if (report.getStatus() == ReportStatus.PENDING){
                if (!report.getReportingUser().getEmail().equals(message.getUser())){
                    report.setStatus(ReportStatus.UNDER_REVIEW);
                    reportRepository.save(report);
                }
            }
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

        mailService.AssignNotificationMessage(employee.getEmail(), report.getTitle(), report.getReportingUser().getCompany().getName(),
                report.getDateAdded().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")),report.getReportingUser().getFirstName());
    }
    @Transactional
    public void saveNewReport(Report report) {
        reportRepository.save(report);
    }

    public ChatMessage getLastMessage(Report report) {
        List<ChatMessage> messages = report.getMessages();
        return messages.get(messages.size() - 1);
    }


    public List<Report> getReportsByCategory(ReportCategory reportCategory) {
        return reportRepository.findAllByCategory(reportCategory);
    }

    public List<Report> getReportsByDateRange(LocalDateTime dateFrom, LocalDateTime dateTo) {
        return reportRepository.findAllByDateAddedIsBetween(dateFrom, dateTo);
    }

    public List<Report> filterReportsByCategory(ReportCategory category) {
        return filterReports(report -> report.getCategory() == category);
    }

    public List<Report> filterReportsByStatus(ReportStatus status) {
        return filterReports(report -> report.getStatus() == status);
    }

    public List<Report> filterReportsByAssignedEmployee(List<Report> reports, User employee) {
        return reports.stream()
                .filter(report -> report.getAssignedUser() == employee)
                .collect(Collectors.toList());
    }

    public List<Report> getReportsByCategoryAndStatus(ReportCategory category, ReportStatus status) {
        List<Report> reportsByCategory = filterReportsByCategory(category);
        List<Report> reportsByStatus = filterReportsByStatus(status);
        reportsByCategory.retainAll(reportsByStatus);
        return reportsByCategory;
    }

    private List<Report> filterReports(Predicate<Report> predicate) {
        return getAllReports().stream()
                .filter(predicate)
                .collect(Collectors.toList());
    }

    public String sortReports(List<Report> reports, String sort) {
        return switch (sort) {
            case "remainingTimeAsc" -> {
                reports.sort(Comparator.<Report, Boolean>comparing(
                        report -> report.getRemainingTime(checkIsForFirstResponse(report)).isExpired() || report.getStatus().equals(ReportStatus.COMPLETED)
                ).thenComparing(
                        report -> report.getRemainingTime(checkIsForFirstResponse(report)),
                        Comparator.comparing(RemainingTime::isExpired)
                                .thenComparing(RemainingTime::getDays)
                                .thenComparing(RemainingTime::getHours)
                                .thenComparing(RemainingTime::getMinutes)
                ));
                yield "Pozostały czas do końca (rosnąco)";
            }
            case "remainingTimeDesc" -> {
                reports.sort(Comparator.<Report, Boolean>comparing(
                        report -> report.getRemainingTime(checkIsForFirstResponse(report)).isExpired() || report.getStatus().equals(ReportStatus.COMPLETED)
                ).thenComparing(
                        report -> report.getRemainingTime(checkIsForFirstResponse(report)),
                        Comparator.comparing(RemainingTime::isExpired).reversed()
                                .thenComparing(RemainingTime::getDays, Comparator.reverseOrder())
                                .thenComparing(RemainingTime::getHours, Comparator.reverseOrder())
                                .thenComparing(RemainingTime::getMinutes, Comparator.reverseOrder())
                ));
                yield "Pozostały czas do końca (malejąco)";
            }
            case "addedDateAsc" -> {
                reports.sort(Comparator.comparing(Report::getDateAdded));
                yield "Data zgłoszenia (od najstarszych)";
            }
            case "addedDateDesc" -> {
                reports.sort(Comparator.comparing(Report::getDateAdded).reversed());
                yield "Data zgłoszenia (od najnowszych)";
            }
            default -> "Brak";
        };
    }

    private boolean checkIsForFirstResponse(Report report) {
        return report.getStatus() == ReportStatus.PENDING;
    }

    @Transactional
    public void changeReportStatus(Long reportId, ReportStatus status) {
        Report report = reportRepository.findById(reportId).orElseThrow();
        report.setStatus(status);
    }

    @Transactional
    public void closeReport(Long reportId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("Report not found"));
        report.setStatus(ReportStatus.COMPLETED);
        reportRepository.save(report);
    }

    @Transactional
    public void deleteReport(Long reportId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("Report not found"));

        List<ChatMessage> messages = chatMessageRepository.findAllByReportId(reportId);
        chatMessageRepository.deleteAll(messages);


        reportRepository.delete(report);
    }


    public FinalListViewDto prepareListForAdmins(int Page, int PageSize, String ListCategory, String Search, String SortBy, String SortOrder) {

        List<Report> result = switch (ListCategory) {
            case "Pending" -> reportRepository.findAllByStatus(ReportStatus.PENDING);
            case "UnderReview" -> reportRepository.findAllByStatus(ReportStatus.UNDER_REVIEW);
            case "NotAssigned" -> reportRepository.findAllByAssignedUserIsNullAndStatusNot(ReportStatus.COMPLETED);
            case "Completed" -> reportRepository.findAllByStatus(ReportStatus.COMPLETED);
            default -> reportRepository.findAllByStatusNot(ReportStatus.COMPLETED);
        };

        //Filtrowanie


        FinalListViewDto finalListViewDto = new FinalListViewDto();
        finalListViewDto.setElement(getListViewDtos(Page, PageSize, Search, SortBy, SortOrder, result));
        finalListViewDto.setShowTimetoClose(true);
        finalListViewDto.setTotalCompleted(reportRepository.countAllByStatus(ReportStatus.COMPLETED));
        finalListViewDto.setTotalPending(reportRepository.countAllByStatus(ReportStatus.PENDING));
        finalListViewDto.setTotalUnderReview(reportRepository.countAllByStatus(ReportStatus.UNDER_REVIEW));
        finalListViewDto.setTotalUnAssigned(reportRepository.countAllByAssignedUserIsNullAndStatusNot(ReportStatus.COMPLETED));

        return finalListViewDto;

    }

    public FinalListViewDto prepareListForEmployees(String UserName ,int Page, int PageSize, String ListCategory, String Search, String SortBy, String SortOrder){
        List<Report> result;
        User user = userRepository.findByEmail(UserName).orElseThrow();
        result = switch (ListCategory) {
            case "Pending" -> reportRepository.findAllByAssignedUserAndStatus(user, ReportStatus.PENDING);
            case "UnderReview" -> reportRepository.findAllByAssignedUserAndStatus(user, ReportStatus.UNDER_REVIEW);
            case "Completed" -> reportRepository.findAllByAssignedUserAndStatus(user, ReportStatus.COMPLETED);
            default -> reportRepository.findAllByAssignedUserAndStatusNot(user, ReportStatus.COMPLETED);
        };
        //Filtrowanie
        FinalListViewDto finalListViewDto = new FinalListViewDto();
        finalListViewDto.setElement(getListViewDtos(Page, PageSize, Search, SortBy, SortOrder, result));
        finalListViewDto.setShowTimetoClose(true);
        finalListViewDto.setTotalCompleted(reportRepository.countAllByAssignedUserAndStatus(user, ReportStatus.COMPLETED));
        finalListViewDto.setTotalPending(reportRepository.countAllByAssignedUserAndStatus(user, ReportStatus.PENDING));
        finalListViewDto.setTotalUnderReview(reportRepository.countAllByAssignedUserAndStatus(user, ReportStatus.UNDER_REVIEW));
        finalListViewDto.setTotalUnAssigned(0);

        return finalListViewDto;
    }

    public FinalListViewDto prepareListForUsers(String UserName, int Page, int PageSize, String ListCategory, String Search, String SortBy, String SortOrder){
        List<Report> result;
        User user = userRepository.findByEmail(UserName).orElseThrow();
        result = switch (ListCategory) {
            case "Pending" -> reportRepository.findAllByReportingUserAndStatus(user, ReportStatus.PENDING);
            case "UnderReview" -> reportRepository.findAllByReportingUserAndStatus(user, ReportStatus.UNDER_REVIEW);
            case "Completed" -> reportRepository.findAllByReportingUserAndStatus(user, ReportStatus.COMPLETED);
            default -> reportRepository.findAllByReportingUserAndStatusNot(user, ReportStatus.COMPLETED);
        };
        //Filtrowanie

        FinalListViewDto finalListViewDto = new FinalListViewDto();
        finalListViewDto.setElement(getListViewDtos(Page, PageSize, Search, SortBy, SortOrder, result));
        finalListViewDto.setShowTimetoClose(false);
        finalListViewDto.setTotalCompleted(reportRepository.countAllByReportingUserAndStatus(user, ReportStatus.COMPLETED));
        finalListViewDto.setTotalPending(reportRepository.countAllByReportingUserAndStatus(user, ReportStatus.PENDING));
        finalListViewDto.setTotalUnderReview(reportRepository.countAllByReportingUserAndStatus(user, ReportStatus.UNDER_REVIEW));
        finalListViewDto.setTotalUnAssigned(0);

        return finalListViewDto;
    }

    private Page<ListViewDto> getListViewDtos(int Page, int PageSize, String Search, String SortBy, String SortOrder, List<Report> result) {
        if (Search != null) {
            String SearchWord = Search.toLowerCase();

            result = result.stream().filter(report ->
                    (report.getTitle().toLowerCase().contains(SearchWord)) ||
                    (report.getDescription().toLowerCase().contains(SearchWord)) ||
                    (report.getReportingUser().getCompany().getName().toLowerCase().contains(SearchWord)))
                    .collect(Collectors.toList());
        }

        List<ListViewDto> listViewDto = new ArrayList<>();
        for (Report report : result) {
            listViewDto.add(ListViewMapper(report));
        }


        if (SortOrder != null && SortBy!=null) {
            sortList(listViewDto, SortBy, SortOrder);
        }


        PageRequest pageable = PageRequest.of(Page, PageSize);

        int start = (int) pageable.getOffset();
        int end = Math.min(start + PageSize, listViewDto.size());

        List<ListViewDto> pageContent = listViewDto.subList(start, end);

        return new PageImpl<>(pageContent, pageable, listViewDto.size());
    }

    private ListViewDto ListViewMapper (Report report) {
        ListViewDto result = new ListViewDto();
        result.setId(report.getId());
        result.setTitle(report.getTitle());
        result.setDescription(report.getDescription());
        if (report.getCategory2() != null) {
            result.setCategory(report.getCategory2().getName());
        }else {
            result.setCategory("brak");
        }

        result.setStatus(report.getStatus().description);
        result.setDateAdded(report.getDateAdded().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")));
        if (report.getAssignedUser() != null) {
            result.setAssignedEmployee(report.getAssignedUser().getEmail());
        }else {
            result.setAssignedEmployee("brak");
        }
        if (!report.getMessages().isEmpty()) {
            List<ChatMessage> chatMessages = report.getMessages();
            result.setLastMessage(chatMessages.get(chatMessages.size() - 1).getTimestamp().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")));
        }else {
            result.setLastMessage("brak");
        }
        result.setCompany(report.getReportingUser().getCompany().getName());

        if (report.getStatus().equals(ReportStatus.UNDER_REVIEW)) {
            result.setTimeToLeft(Duration.between(LocalDateTime.now(), report.getDueDate()).getSeconds());
            result.setFirstRespond(false);
        }else {

            result.setTimeToLeft(Duration.between(LocalDateTime.now(), report.getTimeToRespond()).getSeconds());
            result.setFirstRespond(true);
        }

        return result;
    }

    private void sortList(List<ListViewDto> listViewDto, String SortBy, String SortOrder) {

        switch (SortBy) {
            case "Title":

                if (SortOrder.equals("Desc")) {
                    listViewDto.sort(Comparator.comparing(ListViewDto::getTitle).reversed());
                }else {
                    listViewDto.sort(Comparator.comparing(ListViewDto::getTitle));
                }
                return;

            case "Description":

                if (SortOrder.equals("Desc")) {
                    listViewDto.sort(Comparator.comparing(ListViewDto::getDescription).reversed());
                }else {
                    listViewDto.sort(Comparator.comparing(ListViewDto::getDescription));
                }
                return;

            case "Category":

                if (SortOrder.equals("Desc")) {
                    listViewDto.sort(Comparator.comparing(ListViewDto::getCategory).reversed());
                }else {
                    listViewDto.sort(Comparator.comparing(ListViewDto::getCategory));
                }
                return;

            case "Status":

                if (SortOrder.equals("Desc")) {
                    listViewDto.sort(Comparator.comparing(ListViewDto::getStatus).reversed());
                }else {
                    listViewDto.sort(Comparator.comparing(ListViewDto::getStatus));
                }
                return;

            case "DateAdded":

                if (SortOrder.equals("Desc")) {
                    listViewDto.sort(Comparator.comparing(ListViewDto::getDateAdded).reversed());
                }else {
                    listViewDto.sort(Comparator.comparing(ListViewDto::getDateAdded));
                }
                return;

            case "AssignedEmployee":

                if (SortOrder.equals("Desc")) {
                    listViewDto.sort(Comparator.comparing(ListViewDto::getAssignedEmployee).reversed());
                }else {
                    listViewDto.sort(Comparator.comparing(ListViewDto::getAssignedEmployee));
                }
                return;

            case "LastMessage":

                if (SortOrder.equals("Desc")) {
                    listViewDto.sort(Comparator.comparing(ListViewDto::getLastMessage).reversed());
                }else {
                    listViewDto.sort(Comparator.comparing(ListViewDto::getLastMessage));
                }
                return;

            case "Company":

                if (SortOrder.equals("Desc")) {
                    listViewDto.sort(Comparator.comparing(ListViewDto::getCompany).reversed());
                }else {
                    listViewDto.sort(Comparator.comparing(ListViewDto::getCompany));
                }
                return;

            default:

        }
    }

    public Map<LocalDate, Double> getAverageFirstReactionTimesForReports(List<Report> reports, LocalDate startDate, LocalDate endDate) {
        return calculateAverageTimes(reports, startDate, endDate, Report::getAddedToFirstReactionDuration);
    }

    public Map<LocalDate, Double> getAverageCompletionTimesForReports(List<Report> reports, LocalDate startDate, LocalDate endDate) {
        return calculateAverageTimes(reports, startDate, endDate, Report::getAddedToCompleteDuration);
    }


    private Map<LocalDate, Double> calculateAverageTimes(List<Report> reports, LocalDate startDate, LocalDate endDate, Function<Report, Double> timeExtractor) {
        Map<LocalDate, List<Double>> groupedTimes = startDate.withDayOfMonth(1)
                .datesUntil(endDate.withDayOfMonth(1).plusMonths(1), Period.ofMonths(1))
                .collect(Collectors.toMap(
                        date -> date,
                        date -> new ArrayList<>()
                ));

        //pogrupowanie czasow wg miesiecy
        reports.stream()
                .filter(report -> !report.getDateAdded().toLocalDate().isBefore(startDate) &&
                        !report.getDateAdded().toLocalDate().isAfter(endDate))
                .forEach(report -> {
                    LocalDate month = report.getDateAdded().toLocalDate().with(TemporalAdjusters.firstDayOfMonth());
                    Double duration = timeExtractor.apply(report);
                    if (duration != null) {
                        groupedTimes.get(month).add(duration);
                    }
                });

        //obliczenie srednich dla każdego miesiaca
        return groupedTimes.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream().mapToDouble(Double::doubleValue).average().orElse(0.0)
                ));
    }

}
