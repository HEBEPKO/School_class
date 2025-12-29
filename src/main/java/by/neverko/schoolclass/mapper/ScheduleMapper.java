package by.neverko.schoolclass.mapper;

import by.neverko.schoolclass.dto.ScheduleDto;
import by.neverko.schoolclass.entity.Schedule;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring",
        uses = {SubjectMapper.class, TeacherMapper.class})
public interface ScheduleMapper {

    ScheduleDto toDto(Schedule schedule);


    Schedule toEntity(ScheduleDto scheduleDto);
}
