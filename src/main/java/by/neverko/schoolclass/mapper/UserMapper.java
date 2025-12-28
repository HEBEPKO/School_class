package by.neverko.schoolclass.mapper;

import by.neverko.schoolclass.dto.CreateUserRequest;
import by.neverko.schoolclass.dto.UserDto;
import by.neverko.schoolclass.dto.UserResponse;
import by.neverko.schoolclass.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {})
public interface UserMapper {
    @Mapping(source = "student.id", target = "studentId")
    @Mapping(source = "classEntity.id", target = "classId")
    UserDto toDto(User user);

    @Mapping(target = "password", ignore = true) // пароль устанавливается вручную
    @Mapping(target = "student", ignore = true)
    @Mapping(target = "classEntity", ignore = true)
    User toEntity(CreateUserRequest request);

    @Mapping(source = "student.id", target = "studentId")
    @Mapping(source = "classEntity.id", target = "classId")
    UserResponse toResponse(User user);

    UserDto toDTOWithClassId(User user);
}
