package pl.projekt_inzynierski;

public enum ReportStatus {

    PENDING("Oczekujące"),
    UNDER_REVIEW("W trakcie przeglądu"),
    COMPLETED("Zakończone");

    final String description;

    ReportStatus(String description) {
        this.description = description;
    }
}
