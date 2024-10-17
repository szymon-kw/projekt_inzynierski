package pl.projekt_inzynierski;

public enum ReportCategory {

    CRITICAL("Krytyczne"),
    MAJOR("Poważne"),
    MINOR("Drobne");

    final String description;

    ReportCategory(String description) {
        this.description = description;
    }
}
