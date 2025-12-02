package by.neverko.schoolclass.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "attendance", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"student_id", "date"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false, foreignKey = @ForeignKey(name = "fk_attendance_student"))
    private User student;

    @Column(nullable = false)
    @NotNull(message = "Дата песещаемости обязателен")
    private LocalDate date = LocalDate.now();

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Статус посещаемости обязателень")
    private Attendance status;
}
