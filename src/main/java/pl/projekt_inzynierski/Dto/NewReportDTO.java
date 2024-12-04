package pl.projekt_inzynierski.Dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class NewReportDTO {

    @NotNull(message = "Pole nie może być puste")
    @Size(min = 15, max = 255,message = "Minimalna długość to 15 znaków, a maksymalna 255")
    private String title;

    @NotNull(message = "Pole nie może być puste")
    @Size(min = 50, message = "Minimalna długość to 50 znaków")
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
