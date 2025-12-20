package by.neverko.schoolclass.dto;

import by.neverko.schoolclass.entity.ClassEntity;
import by.neverko.schoolclass.entity.Role;

public record UserDto(
        Long id,
        Role role,
        String name,
        String email,
        String phone,
        Long studentId,
        ClassEntity classEntityId
) {}
