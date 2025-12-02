package by.neverko.schoolclass.repository;

import by.neverko.schoolclass.entity.Homework;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface HomeworkRepository extends JpaRepository<Homework, Long> {
    // Все ДЗ для класса
    List<Homework> findByClassEntityIdOrderByDueDateDesc(Long classId);

    // ДЗ по классу и предмету
    List<Homework> findByClassEntityIdAndSubjectIdOrderByDueDateDesc(Long classId, Long subjectId);

    // ДЗ с датой сдачи >= сегодня (актуальные)
    @Query("SELECT h FROM Homework h WHERE h.classEntity.id = :classId AND h.dueDate >= :date ORDER BY h.dueDate ASC")
    List<Homework> findRelevantByClassId(Long classId, LocalDate date);
}
