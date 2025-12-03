package by.neverko.schoolclass.controller;

import by.neverko.schoolclass.entity.Homework;
import by.neverko.schoolclass.service.HomeworkService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.annotation.RequestScope;

import java.util.List;

@RestController
@RequestMapping("/api/homework")
@RequiredArgsConstructor
public class HomeworkController {

    private final HomeworkService homeworkService;

    @GetMapping
    public ResponseEntity<List<Homework>> getHomework(
            @RequestParam Long classId,
            @RequestParam(required = false) Long subjectId,
            @RequestHeader("X-User-Id") Long currentUserId
            ) {
        List<Homework> homework;

        if (subjectId != null) {
            homework = homeworkService.getHomeworkForClassAndSubject(classId, subjectId);
        } else {
            homework = homeworkService.getHomeworkForClass(classId);
        }
        return ResponseEntity.ok(homework);
    }

    @PostMapping
    public ResponseEntity<Homework> createHomework(
            @RequestBody Homework homework,
            @RequestHeader("X-User-Id") Long currentUserId
    ) {
        Homework saved = homeworkService.createHomework(currentUserId, homework);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Homework> updateHomework(
            @PathVariable Long id,
            @RequestBody Homework homework,
            @RequestHeader("X-User-Id") Long currentUserId
    ) {
        homework.setId(id);
        Homework updated = homeworkService.updateHomework(currentUserId, id, homework);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Homework> deleteHomework(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long currentUserId
    ) {
        homeworkService.deleteHomework(currentUserId, id);
        return ResponseEntity.noContent().build();
    }
}
