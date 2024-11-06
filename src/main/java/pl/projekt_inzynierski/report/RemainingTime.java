package pl.projekt_inzynierski.report;

 public class RemainingTime {
    private long days;
    private long hours;
    private long minutes;

    public RemainingTime(long days, long hours, long minutes) {
        this.days = days;
        this.hours = hours;
        this.minutes = minutes;
    }

    public long getDays() {
        return days;
    }

    public long getHours() {
        return hours;
    }

    public long getMinutes() {
        return minutes;
    }
}
