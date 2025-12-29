package by.neverko.schoolclass.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "grade")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Grade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false, foreignKey = @ForeignKey(name = "fk_grade_student"))
    private User student;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "subject_id", nullable = false, foreignKey = @ForeignKey(name = "fk_grade_subject"))
    private Subject subject;

    @Column(nullable = false, length = 10)
    @NotBlank(message = "Значение оценки обязательно")
    @Size(max = 10, message = "Оценка не может быть длиннее 10 символов")
    private String value;

    @Column(nullable = false)
    @NotNull(message = "Дата оценки обязательна")
    private LocalDate date = LocalDate.now();

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "teacher_id", nullable = false, foreignKey = @ForeignKey(name = "fk_grade_teacher"))
    private User teacher;
}
