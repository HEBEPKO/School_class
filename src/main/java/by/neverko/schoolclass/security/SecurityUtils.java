package by.neverko.schoolclass.security;

import by.neverko.schoolclass.entity.Role;
import by.neverko.schoolclass.entity.User;
import by.neverko.schoolclass.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SecurityUtils {
    private final UserRepository userRepository;

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
}
