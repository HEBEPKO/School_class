package by.neverko.schoolclass.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import lombok.*;


@Entity
@Table(name = "schedule", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"class_id", "day_of_week", "lesson_number"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "class_id", nullable = false, foreignKey = @ForeignKey(name = "fk_schedule_class"))
    private ClassEntity classEntity;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "subject_id", nullable = false, foreignKey = @ForeignKey(name = "fk_schedule_subject"))
    private Subject subject;

    @Column(name = "day_of_week", nullable = false)
    @Min(value = 1, message = "День недели должен быть от 1 (ПН) до 7 (ВС)")
    @Max(value = 7, message = "День недели должен быть от 1 (ПН) до 7 (ВС)")
    private Long dayOfWeek;

    @Column(name = "lesson_number", nullable = false)
    @Min(value = 1, message = "Номер урока должен быть от 1 до 10")
    @Max(value = 10, message = "Номер урока должен быть от 1 до 10")
    private Long lessonNumber;
}
