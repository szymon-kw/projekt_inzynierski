package pl.projekt_inzynierski.mailing;

public enum TypesOfReminderMail {
    ADM_REACTION("Czas na reakcję się kończy", "poniższych zgłoszeń", "Dla zgłoszeń z brakiem pierwszej reakcji wysyłanych do administracji"),
    EMP_REACTION("Czas na reakcję się kończy", "przypisanych do ciebie zgłoszeń", "Dla zgłoszeń z brakiem pierwszej reakcji wysyłanych do przypisanych pracowników"),
    ADM_ENDS("Czas na rozwiązanie problemu się kończy", "poniższych zgłoszeń", "Dla zgłoszeń z kończącym się czasem rozwiązania problemu wysyłanych do administracji"),
    EMP_ENDS("Czas na rozwiązanie problemu się kończy", "przypisanych do ciebie zgłoszeń", "Dla zgłoszeń z kończącym się czasem rozwiązania problemu wysyłanych do przypisanych pracowników"),
    ADM_All("Lista wszyskich niezamkniętych zgłoszeń","","Dla wsyzskich niezamkniętych zgłoszeń"),
    EMP_ALL("Lista towich niezamkniętych zgłoszeń","","Dla wsyzskich niezamkniętych zgłoszeń przydzielonych do pracownika");
    public final String subject;
    public final String titleFragment;
    public final String description;

    TypesOfReminderMail(String subject, String titleFragment, String description) {
        this.subject = subject;
        this.titleFragment = titleFragment;
        this.description = description;
    }
}
