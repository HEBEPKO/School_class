package by.neverko.schoolclass.service;

import by.neverko.schoolclass.entity.Grade;
import by.neverko.schoolclass.repository.GradeRepository;
import by.neverko.schoolclass.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GradeService {

    private final GradeRepository gradeRepository;
    private final SecurityUtils securityUtils;

    // Учитель/родитель: свои оценки
    public List<Grade> getGradesForStudent(Long studentId) {
        return gradeRepository.findByStudentIdOrderByDateDesc(studentId);
    }

    // Учитель: оценки по своему предмету
    public List<Grade> getGradesForSubject(Long subjectId) {
        return gradeRepository.findBySubjectIdOrderByDateDesc(subjectId);
    }

    // Классный руководитель: все оценки класса
    public List<Grade> getGradesForClass(Long classId) {
        return gradeRepository.findByClassId(classId);
    }

    @Transactional
    public Grade addOrUpdateGrade(Long currentUserId, Grade grade) {
        Long subjectId = grade.getSubject().getId();
        Long classId = grade.getStudent().getClassEntity().getId();
        securityUtils.validateCanEditGrade(currentUserId, subjectId, classId);
        grade.setTeacher(securityUtils.getUserOrThrow(currentUserId));
        return gradeRepository.save(grade);
    }

    @Transactional
    public void deleteGrade(Long currentUserId, Long gradeId) {
        Grade grade = gradeRepository.findById(gradeId)
                .orElseThrow(() -> new IllegalArgumentException("Оценка не найдена"));
        Long subjectId = grade.getSubject().getId();
        Long classId = grade.getStudent().getClassEntity().getId();
        securityUtils.validateCanEditGrade(currentUserId, subjectId, classId);
        gradeRepository.deleteById(gradeId);
    }
}
