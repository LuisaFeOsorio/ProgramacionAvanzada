package application.dto.alojamiento;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record DateRangeDTO(
        @NotNull
        LocalDate desde,

        @NotNull
        LocalDate hasta
) {}