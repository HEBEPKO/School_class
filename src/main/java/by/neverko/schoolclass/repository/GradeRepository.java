package by.neverko.schoolclass.repository;

import by.neverko.schoolclass.entity.Grade;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface GradeRepository extends JpaRepository<Grade, Long> {
    // Все оценки ученика
    List<Grade> findByStudentIdOrderByDateDesc(Long studentId);

    // Оценки ученика по предмету
    List<Grade> findByStudentIdAndSubjectIdOrderByDateDesc(Long studentId, Long subjectId);

    // Оценки по предмету (для учителя)
    List<Grade> findBySubjectIdOrderByDateDesc(Long subjectId);

    // Оценки в классе по предмету
    @Query("SELECT g FROM Grade g " +
            "WHERE g.student.classEntity.id = :classId " +
            "AND g.subject.id = :subjectId " +
            "ORDER BY g.date DESC")
    List<Grade> findByClassAndSubject(Long classId, Long subjectId);

    // Оценки класса по всем предметам (для классного руководителя)
    @EntityGraph(attributePaths = {"student", "subject", "teacher"})
    @Query("SELECT g FROM Grade g " +
            "WHERE g.student.classEntity.id = :classId " +
            "ORDER BY g.student.name, g.subject.name, g.date " +
            "DESC")
    List<Grade> findByClassId(Long classId);

    @Query("SELECT g FROM Grade g " +
            "LEFT JOIN FETCH g.student " +
            "LEFT JOIN FETCH g.subject " +
            "LEFT JOIN FETCH g.teacher " +
            "WHERE g.student.id = :studentId")
    List<Grade> findByStudentIdWithDetails(@Param("studentId") Long studentId);

    // Оценки за период
    List<Grade> findByStudentIdAndDateBetween(Long studentId, LocalDate start, LocalDate end);

    List<Grade> findByStudentId(Long studentId);
    List<Grade> findByTeacherId(Long studentId);
}
