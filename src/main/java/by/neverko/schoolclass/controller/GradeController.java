package by.neverko.schoolclass.controller;

import by.neverko.schoolclass.entity.Grade;
import by.neverko.schoolclass.service.GradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/grade")
@RequiredArgsConstructor
public class GradeController {

    private final GradeService gradeService;

    // Ученик/Родитель: свои оценки
    @GetMapping("/me")
    public ResponseEntity<List<Grade>> getMeGrades(
            @RequestHeader("X-User-Id") Long currentUserId
            // Предполагаем, что currentUserId — это ID ученика или родителя
            // Если родитель — нужно найти studentId. Упростим: пусть фронтенд передаёт studentId напрямую
            // В реальности: if (parent) studentId = parent.getStudent().getId()
    ) {
        return ResponseEntity.ok(gradeService.getGradesForStudent(currentUserId));
    }

    // Ученик / Родитель: оценки конкретного ученика (родитель знает ID ребёнка)
    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<Grade>> getGradesForStudent(
            @PathVariable Long studentId,
            @RequestHeader("X-User-Id") Long currentUserId
    ) {
        // Здесь должна быть проверка: currentUserId — родитель studentId ИЛИ currentUserId == studentId
        // В продакшене добавьте!
        return ResponseEntity.ok(gradeService.getGradesForStudent(studentId));
    }

    // Учитель: оценки по своему предмету
    @GetMapping("/subject/{subjectId}")
    public ResponseEntity<List<Grade>> getGradesForSubject(
            @PathVariable Long subjectId,
            @RequestHeader("X-User-Id") Long currentUserId
    ) {
        return ResponseEntity.ok(gradeService.getGradesForSubject(subjectId));
    }

    // Классный руководитель: все оценки класса
    @GetMapping("/class/classId")
    public ResponseEntity<List<Grade>> getGradesForClass(
            @PathVariable Long classId,
            @RequestHeader("X-User-Id") Long currentUserId
    ) {
        return ResponseEntity.ok(gradeService.getGradesForClass(classId));
    }

    // Добавление/редактирование оценки
    @PostMapping
    public ResponseEntity<Grade> addGrade(
            @RequestBody Grade grade,
            @RequestHeader("X-User-Id") Long currentUserId
    ) {
        Grade saved = gradeService.addOrUpdateGrade(currentUserId, grade);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Grade> updateGrade(
            @PathVariable Long id,
            @RequestBody Grade grade,
            @RequestHeader("X-User-Id") Long currentUserId
    ) {
        Grade update = gradeService.addOrUpdateGrade(currentUserId, grade);
        return ResponseEntity.ok(update);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Grade> deleteGrade(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long currentUserId
    ) {
        gradeService.deleteGrade(currentUserId, id);
        return ResponseEntity.noContent().build();
    }

}
