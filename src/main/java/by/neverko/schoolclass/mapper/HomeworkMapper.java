package by.neverko.schoolclass.mapper;

import by.neverko.schoolclass.dto.HomeworkDto;
import by.neverko.schoolclass.entity.Homework;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface HomeworkMapper {

    HomeworkDto toDto(Homework homework);

    Homework toEntity(HomeworkDto homeworkDto);
}
