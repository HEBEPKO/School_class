package by.neverko.schoolclass.repository;

import by.neverko.schoolclass.entity.Grade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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
    @Query("SELECT g FROM Grade g WHERE g.student.classEntity.id = :classId AND g.subject.id = :subjectId ORDER BY g.date DESC")
    List<Grade> findByClassAndSubject(Long classId, Long subjectId);

    // Оценки класса по всем предметам (для классного руководителя)
    @Query("SELECT g FROM Grade g WHERE g.student.classEntity.id = :classId ORDER BY g.student.name, g.subject.name, g.date DESC")
    List<Grade> findByClassId(Long classId);

    // Оценки за период
    List<Grade> findByStudentIdAndDateBetween(Long studentId, LocalDate start, LocalDate end);

}
