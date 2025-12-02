package by.neverko.schoolclass.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "homework")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Homework {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "subject_id", nullable = false, foreignKey = @ForeignKey(name = "fk_homework_subject"))
    private Subject subject;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "class_id", nullable = false, foreignKey = @ForeignKey(name = "fk_homework_class"))
    private ClassEntity classEntity;

    @Lob
    @Column(nullable = false)
    @NotBlank(message = "Описане домашнего задания обязательно")
    private String description;

    @Column(name = "due_date", nullable = false)
    @NotNull(message = "Дата сдачи обязательна")
    private LocalDate dueDate;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "created_by", nullable = false, foreignKey = @ForeignKey(name = "fk_homework_creator"))
    private User createdBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

}
