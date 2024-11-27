package pl.projekt_inzynierski.Dto;

import java.time.LocalDateTime;

public class ChatQueueDto {
    private long ReportId;
    private LocalDateTime RemindTime;
    private String Sender;
    private String Receiver;
    private String Message;
    private LocalDateTime SentTime;

    public ChatQueueDto() {
    }

    public ChatQueueDto(long reportId) {
        ReportId = reportId;
    }

    public long getReportId() {
        return ReportId;
    }

    public void setReportId(long reportId) {
        ReportId = reportId;
    }

    public LocalDateTime getRemindTime() {
        return RemindTime;
    }

    public void setRemindTime(LocalDateTime remindTime) {
        RemindTime = remindTime;
    }

    public String getSender() {
        return Sender;
    }

    public void setSender(String sender) {
        Sender = sender;
    }

    public String getReceiver() {
        return Receiver;
    }

    public void setReceiver(String receiver) {
        Receiver = receiver;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public LocalDateTime getSentTime() {
        return SentTime;
    }

    public void setSentTime(LocalDateTime sentTime) {
        SentTime = sentTime;
    }
}
