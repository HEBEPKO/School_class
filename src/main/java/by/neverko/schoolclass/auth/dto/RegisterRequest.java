package by.neverko.schoolclass.auth.dto;

import by.neverko.schoolclass.entity.Role;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.Set;

@Data
public class RegisterRequest {
    @NotBlank(message = "Имя обязательно")
    @Size(min = 2, max = 100)
    private String name;

    @NotBlank
    @Email(message = "Некорректный email")
    private String email;

    @Pattern(regexp = "^\\+?[0-9\\s\\-()]{7,20}$", message = "Некорректный номер телефона")
    private String phone;

    @NotNull(message = "Роль обязательна")
    private String password;

    private Role role;

    private Long classId;

    private Long studentId;

    private Set<Long> subjectIds;
}
