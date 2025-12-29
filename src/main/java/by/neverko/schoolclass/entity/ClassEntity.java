package by.neverko.schoolclass.entity;

import by.neverko.schoolclass.mapper.Auditable;
import jakarta.persistence.*;
import lombok.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "class")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClassEntity extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, updatable = true)
    @NotBlank(message = "Имя класс не может быть пустым")
    @Size(min = 1, max = 20, message = "Имя класса должно быть от 1 до 20 символов")
    private String name;

    @OneToMany(mappedBy = "classEntity", fetch = FetchType.LAZY)
    private List<User> students = new ArrayList<>();

    @OneToOne(mappedBy = "classEntity", fetch = FetchType.LAZY)
    private User classTeacher;

}
