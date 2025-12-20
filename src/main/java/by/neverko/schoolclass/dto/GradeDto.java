package by.neverko.schoolclass.dto;

import java.time.LocalDate;


public record GradeDto(
   Long id,
   UserDto studentDto,
   SubjectDto subjectId,
   String value,
   LocalDate date,
   UserDto teacherId
) {}
