package by.neverko.schoolclass.service;

import by.neverko.schoolclass.dto.GradeDto;
import by.neverko.schoolclass.entity.AttendanceStatus;
import by.neverko.schoolclass.entity.Grade;
import by.neverko.schoolclass.entity.User;
import by.neverko.schoolclass.repository.GradeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final GradeService gradeService;
    private final AttendanceService attendanceService;

    public String generatePerformanceReport(Long studentId) {
        User student = new User(); // в реальности загрузка через UserRepository
        // Но для отчета достаточно ID

        List<GradeDto> recentGrades = gradeService.getGradesForStudent(studentId).stream()
                .limit(10)
                .toList();

        LocalDate now = LocalDate.now();
        LocalDate twoWeeksAgo = now.minusWeeks(2);
        long absences = attendanceService.getAbsenceCount(studentId, twoWeeksAgo, now, AttendanceStatus.ABSENT);

        StringBuilder report = new StringBuilder();
        report.append("📊 Отчёт об успеваемости и посещаемости\n");
        report.append("Ученик: ???\n"); // нужно будет подгружать имя, но для примера — пропустим
        report.append("Период: ").append(twoWeeksAgo.format(DateTimeFormatter.ISO_LOCAL_DATE))
                .append(" - ").append(now.format(DateTimeFormatter.ISO_LOCAL_DATE)).append("\n\n");

        if (recentGrades.isEmpty()) {
            report.append("Нет новых оценок.\n");
        } else {
            report.append("Последние оценки:\n");
            for (GradeDto g : recentGrades) {
                report.append(" * ").append(g.getStudentName())
                        .append(": ").append(g.getValue())
                        .append(" (").append(g.getDate().format(DateTimeFormatter.ISO_LOCAL_DATE)).append(")\n");
            }
        }

        report.append("\nПропусков за 2 недели: ").append(absences);

        return report.toString();
    }
}
