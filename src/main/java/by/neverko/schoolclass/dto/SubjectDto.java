package by.neverko.schoolclass.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SubjectDto(
    Long id,

    @NotBlank(message = "Название предмета обязательно")
    @Size(min = 2, max = 100)
    String name
) {}
