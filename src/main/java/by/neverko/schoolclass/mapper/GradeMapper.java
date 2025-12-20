package by.neverko.schoolclass.mapper;

import by.neverko.schoolclass.dto.GradeDto;
import by.neverko.schoolclass.entity.Grade;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface GradeMapper {
    List<GradeDto> toDTOs(List<Grade> grades);


    GradeDto toDto(Grade grade);

    @InheritConfiguration
    Grade toEntity(GradeDto gradeDto);
}
