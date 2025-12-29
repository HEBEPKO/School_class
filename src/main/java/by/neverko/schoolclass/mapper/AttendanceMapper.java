package by.neverko.schoolclass.mapper;

import by.neverko.schoolclass.dto.AttendanceDto;
import by.neverko.schoolclass.entity.Attendance;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AttendanceMapper {

    AttendanceDto toDto(Attendance attendance);


    Attendance toEntity(AttendanceDto attendanceDto);

}
