package by.neverko.schoolclass.dto;

import java.time.LocalDateTime;

public record ClassEntityDto(
      Long id,
      String name,
      LocalDateTime timeCreated
) {}
