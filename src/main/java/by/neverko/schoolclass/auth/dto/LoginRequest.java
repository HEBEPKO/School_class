package by.neverko.schoolclass.auth.dto;

public record LoginRequest(
        String email,
        String password
) {}
