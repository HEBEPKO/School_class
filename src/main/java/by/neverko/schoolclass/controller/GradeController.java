package by.neverko.schoolclass.controller;

import by.neverko.schoolclass.dto.GradeDto;
import by.neverko.schoolclass.entity.Grade;
import by.neverko.schoolclass.service.GradeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/grade")
@RequiredArgsConstructor
@Slf4j
public class GradeController {

    private final GradeService gradeService;

    // Ученик/Родитель: свои оценки
    @GetMapping("/me")
    public ResponseEntity<List<GradeDto>> getMeGrades(
            @RequestHeader("X-User-Id") Long currentUserId
            // Предполагаем, что currentUserId — это ID ученика или родителя
            // Если родитель — нужно найти studentId. Упростим: пусть фронтенд передаёт studentId напрямую
            // В реальности: if (parent) studentId = parent.getStudent().getId()
    ) {
        return ResponseEntity.ok(gradeService.getGradesForStudent(currentUserId));
    }

    // Ученик / Родитель: оценки конкретного ученика (родитель знает ID ребёнка)
//    @GetMapping("/student/{studentId}")
//    public ResponseEntity<List<GradeDto>> getGradesForStudent(
//            @PathVariable Long studentId
//    ) {
//        List<GradeDto> grades = gradeService.getGradesForStudent(studentId);
//        return ResponseEntity.ok(gradeService.getGradesForStudent(studentId));
//    }

    // Учитель: оценки по своему предмету
    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasRole('TEACHER') " +
            "or hasRole('ADMIN') " +
            "or hasRole('STUDENT') " +
            "or hasRole('PARENT')")
    public List<GradeDto> getForStudent(@PathVariable Long studentId) {
        log.info("Попали в контроллер");
        return gradeService.getGradesForStudent(studentId);
    }

    // Классный руководитель: все оценки класса
    @GetMapping("/class/classId")
    public ResponseEntity<List<Grade>> getGradesForClass(
            @PathVariable Long classId,
            @RequestHeader("X-User-Id") Long currentUserId
    ) {
        return ResponseEntity.ok(gradeService.getGradesForClass(classId));
    }

//    // Добавление/редактирование оценки
//    @PostMapping
//    public ResponseEntity<GradeDto> addGrade(
//            @Valid @RequestBody GradeDto dto,
//            @RequestHeader Long currentUserId
//    ) {
//        GradeDto saved = gradeService.createGrade(dto);
//        return ResponseEntity.ok(saved);
//    }

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

    @PostMapping
    @PreAuthorize("hasRole('TEACHER')")
    public GradeDto create(@RequestBody GradeDto dto) {
        return gradeService.createGrade(dto);
    }


}
