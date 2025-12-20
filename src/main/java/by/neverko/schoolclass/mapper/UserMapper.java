package by.neverko.schoolclass.mapper;

import by.neverko.schoolclass.dto.UserDto;
import by.neverko.schoolclass.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = {})
public interface UserMapper {

    UserDto toDto(User user);

    User toEntity(UserDto userDto);


    UserDto toDTOWithClassId(User user);
}
