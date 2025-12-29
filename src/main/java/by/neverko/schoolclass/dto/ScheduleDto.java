package by.neverko.schoolclass.dto;

public record ScheduleDto(
   Long id,
   ClassEntityDto classEntityId,
   SubjectDto subjectDto,
   Integer dayOfWeek,
   Integer lessonNumber
) {}
