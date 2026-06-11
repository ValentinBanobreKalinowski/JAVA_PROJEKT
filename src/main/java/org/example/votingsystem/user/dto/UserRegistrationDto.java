package org.example.votingsystem.user.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserRegistrationDto {
    @NotBlank(message = "Imię jest wymagane")
    private String name;

    @NotBlank(message = "Nazwisko jest wymagane")
    private String surname;

    @NotBlank(message = "Email jest wymagany")
    @Email(message = "Podano niepoprawny format adresu email")
    private String email;

    @NotBlank(message = "Hasło jest wymagane")
    @Size(min = 8, message = "Hasło musi mieć co najmniej 8 znaków")
    private String password;

    @NotBlank(message = "Potwierdzenie hasła jest wymagane")
    private String confirmPassword;

    @NotNull(message = "Data urodzenia jest wymagana")
    @Past(message = "Data urodzenia musi być w przeszłości")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateOfBirth;

    @NotBlank(message = "PESEL jest wymagany")
    @Pattern(regexp = "\\d{11}", message = "PESEL must mieć dokładnie 11 cyfr")
    private String pesel;
}
