package pl.projekt_inzynierski.Dto;

import pl.projekt_inzynierski.mailing.TypesOfReminderMail;
import java.util.List;

public class ToSendReminderDTO {
    private String to;
    private TypesOfReminderMail typesOfReminderMail;
    private List<ReportDTO> reports;

    public List<ReportDTO> getReports() {
        return reports;
    }

    public void setReports(List<ReportDTO> reports) {
        this.reports = reports;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }
    public TypesOfReminderMail getTypesOfReminderMail() {
        return typesOfReminderMail;
    }

    public void setTypesOfReminderMail(TypesOfReminderMail typesOfReminderMail) {
        this.typesOfReminderMail = typesOfReminderMail;
    }

}
