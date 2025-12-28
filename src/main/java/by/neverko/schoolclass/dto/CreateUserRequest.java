package by.neverko.schoolclass.dto;

import by.neverko.schoolclass.entity.Role;

public record CreateUserRequest(
        Role role,
        String name,
        String email,
        String phone,
        String password,
        Long studentId,
        Long classId
) {}
