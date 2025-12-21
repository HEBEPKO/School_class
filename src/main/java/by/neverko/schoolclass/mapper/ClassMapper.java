package by.neverko.schoolclass.mapper;

import by.neverko.schoolclass.dto.ClassEntityDto;
import by.neverko.schoolclass.entity.ClassEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ClassMapper {
    ClassEntityDto toDto(ClassEntity classEntity);

    ClassEntity toEntity(ClassEntityDto classEntityDto);
}
