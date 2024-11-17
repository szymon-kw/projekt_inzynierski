package pl.projekt_inzynierski.Dto;

public class ReportDTO {
    private String reportTitle;
    private String reportingUserName;
    private String reportingCompanyName;
    private String timeToLeft; //np. "3 dni 21 godz. 23 min."

    public String getReportTitle() {
        return reportTitle;
    }

    public void setReportTitle(String reportTitle) {
        this.reportTitle = reportTitle;
    }

    public String getReportingUserName() {
        return reportingUserName;
    }

    public void setReportingUserName(String reportingUserName) {
        this.reportingUserName = reportingUserName;
    }

    public String getReportingCompanyName() {
        return reportingCompanyName;
    }

    public void setReportingCompanyName(String reportingCompanyName) {
        this.reportingCompanyName = reportingCompanyName;
    }

    public String getTimeToLeft() {
        return timeToLeft;
    }

    public void setTimeToLeft(String timeToLeft) {
        this.timeToLeft = timeToLeft;
    }
}
