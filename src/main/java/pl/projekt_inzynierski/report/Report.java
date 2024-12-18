package pl.projekt_inzynierski.report;

import jakarta.persistence.*;
import pl.projekt_inzynierski.chat.ChatMessage;
import pl.projekt_inzynierski.attachment.Attachment;
import pl.projekt_inzynierski.user.User;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToMany
    @JoinColumn(name = "report_id")
    private List<ChatMessage> messages = new ArrayList<>();
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "report_id")
    private List<Attachment> attachments = new ArrayList<>();
    private String title;
    private String description;
    private LocalDateTime dateAdded;
    private LocalDateTime dueDate;
    private LocalDateTime timeToRespond;
    @ManyToOne
    private ReportCategory category;
    @Enumerated(EnumType.STRING)
    private ReportStatus status;
    private String image;
    @ManyToOne
    private User reportingUser;
    @ManyToOne
    private User assignedUser;
    private Double addedToFirstReactionDuration;
    private Double addedToCompleteDuration;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(LocalDateTime dateAdded) {
        this.dateAdded = dateAdded;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public ReportCategory getCategory() {
        return category;
    }

    public void setCategory(ReportCategory category) {
        this.category = category;
    }

    public ReportStatus getStatus() {
        return status;
    }

    public void setStatus(ReportStatus status) {
        this.status = status;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public User getReportingUser() {
        return reportingUser;
    }

    public void setReportingUser(User reportingUser) {
        this.reportingUser = reportingUser;
    }

    public User getAssignedUser() {
        return assignedUser;
    }

    public void setAssignedUser(User assignedUser) {
        this.assignedUser = assignedUser;
    }

    public RemainingTime getRemainingTime(boolean forFirstRespond) {
        Duration duration = Duration.between(LocalDateTime.now(), forFirstRespond? timeToRespond : dueDate);
        long days;
        long hours;
        long minutes;
        boolean isExpired;
        if (duration.getSeconds()<0){
            days = 0;
            hours = 0;
            minutes = 0;
            isExpired = true;
        }else {
            days = duration.toDays();
            hours = duration.toHours() % 24;
            minutes = duration.toMinutes() % 60;
            isExpired = false;
        }

        return new RemainingTime(days, hours, minutes, isExpired);
    }

    public Duration getRemainingTimeDuration() {
        return Duration.between(LocalDateTime.now(), dueDate);
    }

    public List<ChatMessage> getMessages() {
        return messages;
    }

    public void setMessages(List<ChatMessage> messages) {
        this.messages = messages;
    }

    public List<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
    }

    public LocalDateTime getTimeToRespond() {
        return timeToRespond;
    }

    public void setTimeToRespond(LocalDateTime firstRespondTime) {
        this.timeToRespond = firstRespondTime;
    }

    public Double getAddedToFirstReactionDuration() {
        return addedToFirstReactionDuration;
    }

    public void setAddedToFirstReactionDuration() {
        this.addedToFirstReactionDuration = (double) Duration.between(dateAdded, LocalDateTime.now()).toMinutes() / 60.0;
    }

    public Double getAddedToCompleteDuration() {
        return addedToCompleteDuration;
    }

    public void setAddedToCompleteDuration() {
        this.addedToCompleteDuration = (double) Duration.between(dateAdded, LocalDateTime.now()).toMinutes() / 60.0;
    }
}
