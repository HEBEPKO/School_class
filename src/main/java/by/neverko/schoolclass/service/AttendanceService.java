package by.neverko.schoolclass.service;

import by.neverko.schoolclass.entity.Attendance;
import by.neverko.schoolclass.entity.AttendanceStatus;
import by.neverko.schoolclass.repository.AttendanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;

    public List<Attendance> getAttendanceForStudent(Long studentId) {
        return attendanceRepository.findByStudentIdOrderByDateDesc(studentId);
    }

    public long getAbsenceCount(Long studentId, LocalDate from, LocalDate to, AttendanceStatus status) {
        return attendanceRepository.countAbsences(studentId, from, to, status);
    }

    @Transactional
    public Attendance recordAttendance(Long studentId, LocalDate date, String status) {
        // В реальном проекте статус должен приходить как AttendanceStatus
        // Здесь упращено
        Attendance attendance = new Attendance();
        attendance.getStudent().setId(studentId);
        attendance.setDate(date);
        attendance.setStatus(AttendanceStatus.valueOf(status));
        return attendanceRepository.save(attendance);
    }
}
