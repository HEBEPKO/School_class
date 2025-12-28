package by.neverko.schoolclass.auth.dto;

public record AuthResponse(
        String token,
        String role
) {
}
