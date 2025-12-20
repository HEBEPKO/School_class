package by.neverko.schoolclass.mapper;

import by.neverko.schoolclass.dto.UserDto;
import by.neverko.schoolclass.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = {})
public interface UserMapper {
    @Mapping(source = "student.id", target = "studentId")
    @Mapping(source = "classEntity.id", target = "classId")
    UserDto toDto(User user);

    UserDto toDTOWithClassId(User user);
}
