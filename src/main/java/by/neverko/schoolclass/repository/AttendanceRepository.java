package by.neverko.schoolclass.repository;

import by.neverko.schoolclass.entity.Attendance;
import by.neverko.schoolclass.entity.AttendanceStatus;
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
    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.student.id = :studentId AND a.date BETWEEN :start AND :end AND " +
            "a.status = :status")
    long countAbsences(Long studentId, LocalDate start, LocalDate end, AttendanceStatus status);

    // Статус по дате
    Attendance findByStudentIdAndDate(Long studentId, LocalDate date);

}
