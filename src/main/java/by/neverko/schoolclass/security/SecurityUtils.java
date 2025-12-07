package by.neverko.schoolclass.security;

import by.neverko.schoolclass.entity.Role;
import by.neverko.schoolclass.entity.User;
import by.neverko.schoolclass.event.PerformanceReportEvent;
import by.neverko.schoolclass.repository.GradeRepository;
import by.neverko.schoolclass.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import org.springframework.security.access.AccessDeniedException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SecurityUtils {
    private final UserRepository userRepository;
//    private final NotificationService notificationService;
    private final ApplicationEventPublisher eventPublisher;
    private final GradeRepository gradeRepository;


    public User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));
    }

    public void validateIsClassTeacher(Long userId, Long classId) {
        User user = getUserOrThrow(userId);
        if (user.getRole() != Role.CLASS_TEACHER || !user.getClassEntity().getId().equals(classId)) {
            throw new SecurityException("Только классный руководитель может редактировать расписание этого класса");
        }
    }

    public void validateCanEditHomework(Long userId, Long classId, Long subjectId) {
        User user = getUserOrThrow(userId);
        Role role = user.getRole();

        if (role == Role.STUDENT) {
            if (!user.getClassEntity().getId().equals(classId)) {
                throw new SecurityException("Ученик может редактировать ДЗ только своего класса");
            }
        } else if (role == Role.TEACHER || role == Role.CLASS_TEACHER) {
            if (role == Role.TEACHER) {
                boolean teacherSubject = user.getSubjects().stream()
                        .anyMatch(s -> s.getId().equals(subjectId));
                if (!teacherSubject) {
                    throw new SecurityException("Учитель не ведет этот предмет");
                }
            }
            // CLASS_TEACHER может редактировать любое ДЗ своего класса
            if (!user.getClassEntity().getId().equals(classId)) {
                throw new SecurityException("Нет доступа к ДЗ Этого Класса");
            }
        } else {
            throw new SecurityException("Только ученик, учитель или классный руководитель могут редактировать ДЗ");
        }
    }

    public void validateCanEditGrade(Long userId, Long subjectId, Long classId) {
        User user = getUserOrThrow(userId);
        Role role = user.getRole();

        if (role == Role.CLASS_TEACHER) {
            if (!user.getClassEntity().getId().equals(classId)) {
                throw new SecurityException("Классный руководитель может редактировать оценки только своего класса");
            }
            // CLASS_TEACHER может ставить оценки по ЛЮБОМУ предмету в своем классе
            return;
        }

        if (role == Role.TEACHER) {
            boolean teacherSubject = user.getSubjects().stream()
                    .anyMatch(s -> s.getId().equals(subjectId));
            if (!teacherSubject) {
                throw new SecurityException("Учитель не ведет этот предмет");
            }
            return;
        }

        throw new SecurityException("Только учитель или классный руководитель могут редактировать оценки");
    }

    public Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

         Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            String username = ((UserDetails) principal).getUsername();
            try {
                return Long.parseLong(username);
            } catch (NumberFormatException e) {
                // Лучше не полагаться на это
                return null;
            }
        }
        return null;
    }

    public boolean validateCanSendPerformanceReport(Long currentUserId, Long studentId) {
        Optional<User> currentUserOpt = userRepository.findById(currentUserId);

        if (currentUserOpt.isEmpty()) {
            return false;
        }

        User currentUser = currentUserOpt.get();

        if (currentUser.getRole() != Role.TEACHER && currentUser.getRole() != Role.CLASS_TEACHER) {
            return false;
        }

        Optional<User> studentOpt = userRepository.findById(studentId);
        if (studentOpt.isEmpty()) {
            return false;
        }

        User student = studentOpt.get();

        if (currentUser.getRole() == Role.CLASS_TEACHER &&
            currentUser.getClassEntity() != null &&
            student.getClassEntity() != null &&
            currentUser.getClassEntity().equals(student.getClassEntity())) {
            return true;
        }

        return gradeRepository.existsByStudentIdAndTeacherId(studentId, currentUserId);
    }

    public boolean canSendPerformanceReport(Long currentUserId, Long studentId) {
        Optional<User> currentUserOpt = userRepository.findById(currentUserId);
        if (currentUserOpt.isEmpty()) {
            return false;
        }

        User currentUser = currentUserOpt.get();

        // Проверяем, является ли пользователь учителем или классным руководителем
        if (!Role.TEACHER.equals(currentUser.getRole()) && !Role.CLASS_TEACHER.equals(currentUser.getRole())) {
            return false;
        }

        // Проверяем, относится ли ученик к классу текущего пользователя
        // или текущий пользователь преподает предметы ученику
        return true;
    }
}
