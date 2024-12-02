package pl.projekt_inzynierski.Dto;

import jakarta.validation.constraints.NotNull;
import pl.projekt_inzynierski.CastomValidators.Password;

public class PasswordsDTO {
    @NotNull
    @Password
    private String password;
    @NotNull
    private String confirmPassword;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public @NotNull String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}

