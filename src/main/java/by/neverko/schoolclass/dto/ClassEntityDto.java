package by.neverko.schoolclass.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record ClassEntityDto(
      Long id,
      @NotBlank(message = "Имя класса обязательно")
      @Size(min = 1, max = 20, message = "Имя класса: 1–20 символов")
      String name,
      LocalDateTime timeCreated
) {}
