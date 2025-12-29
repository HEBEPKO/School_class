package by.neverko.schoolclass.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class GradeDto {
    private Long id;
    @NotNull(message = "ID ученика обязателен")
    private Long studentId;
    @NotNull(message = "ID предмета обязателен")
    private Long subjectId;
    @NotBlank(message = "Оценка обязательна")
    @Size(max = 10)
    private String value;
    @NotNull(message = "Дата обязательна")
    private LocalDate date = LocalDate.now();

    private Long teacherId;

    private String studentName;
    private String subjectName;
    private String teacherName;
}
