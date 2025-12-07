package by.neverko.schoolclass.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "\"user\"")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    @NotBlank(message = "Пороль обязателен", groups = OnCreate.class)
    @Size(min = 6, message = "Пароль должен быть не короче 6 символов")
    @JsonIgnore
    private String password;

    @Column(name = "telegram_chat_id")
    private String telegramChatId;

    // Для валидации при создании
    public interface OnCreate {}
}
