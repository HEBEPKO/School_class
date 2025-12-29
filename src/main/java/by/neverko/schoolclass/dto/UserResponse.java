package by.neverko.schoolclass.dto;

import by.neverko.schoolclass.entity.Role;

public record UserResponse(
        Long id,
        Role role,
        String name,
        String email,
        String phone,
        Long studentId,
        Long classId
) {}
