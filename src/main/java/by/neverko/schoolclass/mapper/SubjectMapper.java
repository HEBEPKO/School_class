package by.neverko.schoolclass.mapper;

import by.neverko.schoolclass.dto.SubjectDto;
import by.neverko.schoolclass.entity.Subject;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SubjectMapper {
    SubjectDto toDto(Subject subject);

    Subject toEntity(SubjectDto subjectDto);
}
