package pl.projekt_inzynierski.Dto;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class NewReportDTO {
    private String title;
    private String description;
    private List<MultipartFile> file;
    // kategory {not implemented}

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

    public List<MultipartFile> getFile() {
        return file;
    }

    public void setFile(List<MultipartFile> file) {
        this.file = file;
    }
}
