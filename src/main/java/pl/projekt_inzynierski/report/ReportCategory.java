package pl.projekt_inzynierski.report;

public enum ReportCategory {

    CRITICAL("Krytyczne"),
    MAJOR("Poważne"),
    MINOR("Drobne");

    public final String description;

    ReportCategory(String description) {
        this.description = description;
    }
}
