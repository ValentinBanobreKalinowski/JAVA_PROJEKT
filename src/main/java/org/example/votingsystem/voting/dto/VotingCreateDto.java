package org.example.votingsystem.voting.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import org.example.votingsystem.voting.model.VotingType;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VotingCreateDto {

    @NotBlank(message = "Tytuł głosowania jest wymagany")
    @Size(min = 5, max = 150, message = "Tytuł musi mieć od 5 do 150 znaków")
    private String title;

    private String description;

    @NotNull(message = "Data rozpoczęcia jest wymagana")
    @FutureOrPresent(message = "Data rozpoczęcia nie może być w przeszłości")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime startTime;

    @NotNull(message = "Data zakończenia jest wymagana")
    @Future(message = "Data zakończenia musi być w przyszłości")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime endTime;

    @NotNull(message = "Wybierz typ głosowania")
    private VotingType type;

    @NotEmpty(message = "Głosowanie musi mieć opcje")
    @Size(min = 2, message = "Musisz dodać co najmniej 2 opcje")
    private List<@NotBlank(message = "Opcja nie może być pusta") String> options;
}