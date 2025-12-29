package by.neverko.schoolclass.service;

import by.neverko.schoolclass.auth.CurrentUserService.CurrentUserService;
import by.neverko.schoolclass.dto.GradeDto;
import by.neverko.schoolclass.entity.Grade;
import by.neverko.schoolclass.entity.Role;
import by.neverko.schoolclass.entity.Subject;
import by.neverko.schoolclass.entity.User;
import by.neverko.schoolclass.exception.ResourceNotFoundException;
import by.neverko.schoolclass.mapper.GradeMapper;
import by.neverko.schoolclass.repository.GradeRepository;
import by.neverko.schoolclass.repository.SubjectRepository;
import by.neverko.schoolclass.repository.UserRepository;
import by.neverko.schoolclass.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class GradeService {

    private final GradeRepository gradeRepository;
    private final SecurityUtils securityUtils;
    private final UserRepository userRepository;
    private final SubjectRepository subjectRepository;
    private final GradeMapper gradeMapper;
    private final CurrentUserService currentUserService;

    // Учитель/родитель: свои оценки
    public List<GradeDto> getGradesForStudent(Long studentId) {
        log.info("Попали в сервис");
        User currentUser = currentUserService.getCurrentUser();

        User targetStudent = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Ученик не найден"));

        checkPermissions(currentUser, studentId);

        List<Grade> grades = gradeRepository.findByStudentId(studentId);
//        return grades.stream()
//                .map(grade -> {
//                    GradeDto dto = gradeMapper.toDto(grade);
//                    enrichGradeDto(dto, grade);
//                    return dto;
//                })
//                .collect(Collectors.toList());

        // Способ 2: Через отдельный метод (более чистый)
         return grades.stream()
                 .map(this::convertToDto)
                 .collect(Collectors.toList());
    }


    // Учитель: оценки по своему предмету
    public List<Grade> getGradesForSubject(Long subjectId) {
        return gradeRepository.findBySubjectIdOrderByDateDesc(subjectId);
    }

    // Классный руководитель: все оценки класса
    public List<Grade> getGradesForClass(Long classId) {
        return gradeRepository.findByClassId(classId);
    }

    public Grade addOrUpdateGrade(Long currentUserId, Grade grade) {
        Long subjectId = grade.getSubject().getId();
        Long classId = grade.getStudent().getClassEntity().getId();
        securityUtils.validateCanEditGrade(currentUserId, subjectId, classId);
        grade.setTeacher(securityUtils.getUserOrThrow(currentUserId));
        return gradeRepository.save(grade);
    }
    public void deleteGrade(Long currentUserId, Long gradeId) {
        Grade grade = gradeRepository.findById(gradeId)
                .orElseThrow(() -> new IllegalArgumentException("Оценка не найдена"));
        Long subjectId = grade.getSubject().getId();
        Long classId = grade.getStudent().getClassEntity().getId();
        securityUtils.validateCanEditGrade(currentUserId, subjectId, classId);
        gradeRepository.deleteById(gradeId);
    }

    public GradeDto createGrade(GradeDto dto) {

        User teacher = currentUserService.getCurrentUser();

        log.info("Teacher {} выставляет оценку student {} по subject {}",
                teacher.getEmail(), dto.getStudentId(), dto.getSubjectId());

        if (teacher.getRole() != Role.TEACHER) {
            throw new SecurityException("Только учитель может ставить оценки");
        }

        User student = userRepository.findById(dto.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Ученик не найден"));
        Subject subject = subjectRepository.findById(dto.getSubjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Предмет не найден"));

        if (!teacher.getSubjects().contains(subject)) {
            throw new SecurityException("Вы не преподаёте этот предмет");
        }

        // Дополнительная проверка: учитель преподаёт в классе ученика?
//         if (!teacher.getClassEntity().contains(student.getClassEntity())) {
//             throw new SecurityException("Ученик не в вашем классе");
//         }

        // Добавить позже
        // Проверка: ученик учится в классе, где есть этот предмет?
        // (Упрощённая логика: если учитель ведёт предмет, он может ставить оценку любому ученику,
        // но лучше проверить расписание или класс)
        // Альтернатива: проверка через Schedule — но для MVP пропустим

        Grade grade = gradeMapper.toEntity(dto);
        grade.setStudent(student);
        grade.setSubject(subject);
        grade.setTeacher(teacher);
        grade.setDate(dto.getDate() != null ? dto.getDate() : LocalDate.now());

        grade = gradeRepository.save(grade);
        return convertToDto(grade);
    }

    private void enrichGradeDto(GradeDto dto, Grade grade) {
        dto.setStudentName(grade.getStudent().getName());
        dto.setSubjectName(grade.getSubject().getName());
        dto.setTeacherName(grade.getTeacher().getName());
    }

    private GradeDto convertToDto(Grade grade) {
        GradeDto dto = gradeMapper.toDto(grade);
        enrichGradeDto(dto, grade);
        return dto;
    }

    private void checkPermissions(User currentUser, Long studentId) {
        switch (currentUser.getRole()) {
            case STUDENT:
                if (!currentUser.getId().equals(studentId)) {
                    throw new SecurityException("Вы можете просматривать только свои оценки");
                }
                break;

            case PARENT:
                if (currentUser.getStudent() == null ||
                        !currentUser.getStudent().getId().equals(studentId)) {
                    throw new SecurityException("Вы можете просматривать оценки только своего ребёнка");
                }
                break;

            case TEACHER:


                break;

            case ADMIN:

                break;

            default:
                throw new SecurityException("Доступ запрещён");
        }
    }

}
