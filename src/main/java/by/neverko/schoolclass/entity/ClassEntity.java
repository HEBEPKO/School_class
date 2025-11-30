package by.neverko.schoolclass.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "class")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClassEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, updatable = true)
    private String name;

    @OneToMany(mappedBy = "classEntity", fetch = FetchType.LAZY)
    private List<User> students = new ArrayList<>();

    @OneToOne(mappedBy = "classEntity", fetch = FetchType.LAZY)
    private User classTeacher;
}
