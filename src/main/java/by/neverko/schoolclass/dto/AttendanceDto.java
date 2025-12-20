package by.neverko.schoolclass.dto;

import by.neverko.schoolclass.entity.AttendanceStatus;

import java.time.LocalDate;

public record AttendanceDto(
   Long id,
   UserDto studentId,
   LocalDate date,
   AttendanceStatus status
) {}
