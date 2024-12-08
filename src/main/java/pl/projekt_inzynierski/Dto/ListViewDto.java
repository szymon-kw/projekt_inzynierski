package pl.projekt_inzynierski.Dto;

public class ListViewDto {
    private long id;
    private String title;
    private String description;
    private String category;
    private String status;
    private String dateAdded;
    private String assignedEmployee;
    private String lastMessage;
    private String company;
    private long timeToLeft;
    private boolean firstRespond;


    public long getId() {
        return id;
    }

    public void setId(long id) {
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(String dateAdded) {
        this.dateAdded = dateAdded;
    }

    public String getAssignedEmployee() {
        return assignedEmployee;
    }

    public void setAssignedEmployee(String assignedEmployee) {
        this.assignedEmployee = assignedEmployee;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public long getTimeToLeft() {
        return timeToLeft;
    }

    public void setTimeToLeft(long timeToLeft) {
        this.timeToLeft = timeToLeft;
    }

    public boolean isFirstRespond() {
        return firstRespond;
    }

    public void setFirstRespond(boolean firstRespond) {
        this.firstRespond = firstRespond;
    }
}
