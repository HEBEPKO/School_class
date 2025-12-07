package by.neverko.schoolclass.service;

import by.neverko.schoolclass.entity.AttendanceStatus;
import by.neverko.schoolclass.entity.Grade;
import by.neverko.schoolclass.entity.Role;
import by.neverko.schoolclass.entity.User;
import by.neverko.schoolclass.event.PerformanceReportEvent;
import by.neverko.schoolclass.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final GradeService gradeService;
    private final AttendanceService attendanceService;
    private final UserRepository userRepository;
    private final TelegramBotService telegramBotService;

    @EventListener
    public void handlePerformanceReportEvent(PerformanceReportEvent event) {
        sendPerformanceReportParents(event.getStudentId());
    }

    public String generatePerformanceReport(Long studentId) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new NoSuchElementException("Ученик не найден"));

        // Получаем информацию о классе
        String className = student.getClassEntity() != null ? student.getClassEntity().getName() : "Не указан";

        // Получаем оценки
        List<String> grades = gradeService.getGradesForStudent(studentId)
                .stream()
                .limit(10)
                .map(g -> g.getSubject().getName() + ": " + g.getValue() + " (" + g.getDate() + ")")
                .collect(Collectors.toList());

        // Получаем посещаемость
        LocalDate now = LocalDate.now();
        LocalDate twoWeeksAgo = now.minusWeeks(2);
        long absences = attendanceService.getAbsenceCount(studentId, twoWeeksAgo, now, AttendanceStatus.ABSENT);
        // Формируем отчет
        return String.format(
                "Ученик: %s\nКласс: %s\n\nПоследние оценки:\n%s\n\nПосещаемость:\nПропусков: %d\nОпозданий: ",
                student.getName(),
                className,
                String.join("\n", grades),
                absences
        );
    }

    public void sendPerformanceReportParent (Long studentId) {
        // 1. Получаем ученика (для имени)
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Ученик не найден"));

        // 2. Находим всех родителей ученика
        List<User> parents = userRepository.findByStudentIdAndRole(studentId, Role.PARENT);

        if (parents.isEmpty()) {
            throw new IllegalArgumentException("У ученика нет зарегистрированных родителей");
        }

        // 3. Генерируем отчёт
        String report = buildReport(student, studentId);

        // 4. Отправляем каждому родителю
        for (User parent : parents) {
            if (parent.getTelegramChatId() != null) {
                telegramBotService.sendMessage(parent.getTelegramChatId(), report);
            }
        }
    }

    public boolean sendPerformanceReportParents(Long studentId) {
        String report = generatePerformanceReport(studentId);

        List<User> parents = userRepository.findParentsByStudentId(studentId);

        boolean allSent = true;
        for (User parent : parents) {
            if(parent.getTelegramChatId() != null) {
                try {
                    telegramBotService.sendMessage(parent.getTelegramChatId(), report);
                } catch (Exception e) {
                    allSent = false;
                }
            }
        }
        return allSent;
    }

    public boolean canAccessStudentData(Long teacherId, Long studentId) {
        User teacher = userRepository.findByIdAndRole(teacherId, Role.TEACHER)
                .orElseThrow(() -> new IllegalArgumentException("Учитель не найден"));

        User student = userRepository.findByIdAndRole(studentId, Role.STUDENT)
                .orElseThrow(() -> new IllegalArgumentException("Ученик не найден"));

        if (student.getClassEntity() != null &&
                student.getClassEntity().getClassTeacher() != null &&
                student.getClassEntity().getClassTeacher().getId().equals(teacherId)) {
            return true;
        }

        return userRepository.isTeacherTeachingStudent(teacherId, studentId);
    }

    private String buildReport(User student, Long studentId) {
        var recentGrades = gradeService.getGradesForStudent(studentId).stream()
                .limit(10)
                .toList();

        LocalDate now = LocalDate.now();
        LocalDate twoWeeksAgo = now.minusWeeks(2);
        long absences = attendanceService.getAbsenceCount(studentId, twoWeeksAgo, now, AttendanceStatus.ABSENT);

        StringBuilder report = new StringBuilder();
        report.append("\uD83D\uDCCA *Отчёт об успеваемости и посещаемости*\\n");
        report.append("Ученик: *").append(student.getName()).append("*\n");
        report.append(": ").append(twoWeeksAgo.format(DateTimeFormatter.ISO_LOCAL_DATE))
                .append(" - ").append(now.format(DateTimeFormatter.ISO_LOCAL_DATE)).append("\n\n");

        if (recentGrades.isEmpty()) {
            report.append("Нет новых оценок за последние 2 недели.\\n");
        } else {
            report.append("Последние оценки:\\n");
            for (var grade : recentGrades) {
                report.append(" * ").append(grade.getSubject().getName()).append("*: ")
                        .append(grade.getValue())
                        .append(" (").append(grade.getDate().format(DateTimeFormatter.ISO_LOCAL_DATE)).append(")\n");
            }
        }

        report.append("\nПропусков (неявка): *").append(absences).append("*");

        return report.toString();
    }

}
