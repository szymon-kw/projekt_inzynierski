package pl.projekt_inzynierski.Dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;

public class EmailUpdateDTO {

    @NotEmpty(message = "Email nie może być pusty.")
    @Email(message = "Nieprawidłowy format emaila.")
    private String email;

    @NotEmpty(message = "Hasło nie może być puste.")
    private String currentPassword;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }
}
