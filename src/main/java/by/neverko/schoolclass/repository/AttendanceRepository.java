package by.neverko.schoolclass.repository;

import by.neverko.schoolclass.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    List<Attendance> findByStudentIdOrderByDateDesc(Long studentId);

    List<Attendance> findByStudentIdAndDateBetween(Long studentId, LocalDate start, LocalDate end);

    // Количество пропусков за период
    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.studentId = :studentId AND a.date BETWEEN :start AND :end AND " +
            "a.status = by.neverko.schoolclass.entity.AttendanceStatus.ABSENT")
    long countAbsences(Long studentId, LocalDate start, LocalDate end);

    // Статус по дате
    Attendance findByStudentIdAndDate(Long studentId, LocalDate date);

}
