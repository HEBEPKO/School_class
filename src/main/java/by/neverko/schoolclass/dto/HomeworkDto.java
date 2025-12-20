package by.neverko.schoolclass.dto;

import by.neverko.schoolclass.entity.ClassEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record HomeworkDto(
   Long id,
   SubjectDto subjectId,
   ClassEntity classEntityId,
   String description,
   LocalDate dueDate,
   UserDto createdBy,
   LocalDateTime createdAt
) {}
