package by.neverko.schoolclass.service;

import by.neverko.schoolclass.entity.Homework;
import by.neverko.schoolclass.repository.HomeworkRepository;
import by.neverko.schoolclass.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HomeworkService {

    private final HomeworkRepository homeworkRepository;
    private final SecurityUtils securityUtils;

    public List<Homework> getHomeworkForClass(Long classId) {
        return homeworkRepository.findByClassEntityIdOrderByDueDateDesc(classId);
    }

    public List<Homework> getHomeworkForClassAndSubject(Long classId, Long subject) {
        return homeworkRepository.findByClassEntityIdAndSubjectIdOrderByDueDateDesc(classId, subject);
    }

    @Transactional
    public Homework updateHomework(Long currentUserId, Long homeworkId, Homework updated) {
        Homework existing = homeworkRepository.findById(homeworkId)
                .orElseThrow(() -> new IllegalArgumentException("Домашнее задание не найдено"));
        securityUtils.validateCanEditHomework(currentUserId, existing.getClassEntity().getId(), existing.getSubject().getId());
        existing.setDescription(updated.getDescription());
        existing.setDueDate(updated.getDueDate());
        existing.setSubject(updated.getSubject());
        existing.setClassEntity(updated.getClassEntity());
        return homeworkRepository.save(existing);
    }

    @Transactional
    public void deleteHomework(Long currentUserId, Long homeworkId) {
        Homework homework = homeworkRepository.findById(homeworkId)
                .orElseThrow(() -> new IllegalArgumentException("Домашнее задание не найдено"));
        securityUtils.validateCanEditHomework(currentUserId, homework.getClassEntity().getId(), homework.getSubject().getId());
        homeworkRepository.deleteById(homeworkId);
    }
}
