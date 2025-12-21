package by.neverko.schoolclass.entity;

import by.neverko.schoolclass.mapper.Auditable;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "\"user\"")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(nullable = false)
    @NotBlank(message = "Имя пользователя обязательно")
    @Size(min = 2, max = 100, message = "Имя должно содержать от 2 до 100 символов")
    private String name;

    @Column(unique = true)
    @Email(message = "Некорректный email")
    private String email;

    @Pattern(regexp = "^\\+?[0-9\\s\\-()]{7,20}$", message = "Некорректный номер телефона")
    private String phone;

    // Связь: родитель → ученик
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", foreignKey = @ForeignKey(name = "fk_user_student"))
    private User student;

    // Класс (для STUDENT и CLASS_TEACHER)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id", foreignKey = @ForeignKey(name = "fk_user_class"))
    private ClassEntity classEntity;

    // --- Обратные связи ---

    // Для ученика: его родители
    @OneToMany(mappedBy = "student", fetch = FetchType.LAZY)
    private List<User> parents = new ArrayList<>();

    // Предметы, которые ведёт учитель (связь через промежуточную таблицу)
    @ManyToMany
    @JoinTable(
            name = "teacher_subject",
            joinColumns = @JoinColumn(name = "teacher_id"),
            inverseJoinColumns = @JoinColumn(name = "subject_id")
            )
    private List<Subject> subjects = new ArrayList<>();

    // Домашние задания, созданные этим пользователем
    @OneToMany(mappedBy = "createdBy", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Homework> createdHomeworks = new ArrayList<>();

    // Оценки, выставленные этим учителем
    @OneToMany(mappedBy = "teacher", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Grade> givenGrades = new ArrayList<>();

}
