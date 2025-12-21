package by.neverko.schoolclass.controller;

import by.neverko.schoolclass.dto.ClassEntityDto;
import by.neverko.schoolclass.entity.ClassEntity;
import by.neverko.schoolclass.service.ClassEntityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/class")
@RequiredArgsConstructor
public class ClassEntityController {

    private final ClassEntityService classEntityService;

    @GetMapping("/")
    public ResponseEntity<List<ClassEntityDto>> getAllClass() {

        return ResponseEntity.ok(classEntityService.getAllClass());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClassEntityDto> getClassId(@PathVariable Long id) {

        return ResponseEntity.ok(classEntityService.getClassId(id));
    }

    @GetMapping("/search")
    public ResponseEntity<ClassEntityDto> getClassName(@RequestParam String name) {

        return ResponseEntity.ok(classEntityService.getClassName(name));
    }

    @PostMapping("/add")
    public ResponseEntity<ClassEntityDto> createdClassEntity(@RequestBody ClassEntityDto classEntityDto) {
        ClassEntityDto classDto = classEntityService.createdClass(classEntityDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(classDto);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ClassEntityDto> updateClassEntity(@PathVariable Long id, @RequestBody ClassEntityDto classEntityDto) {

        ClassEntityDto updateClass = classEntityService.update(id, classEntityDto);
        return ResponseEntity.ok(updateClass);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteClass(@PathVariable Long id) {
        classEntityService.deleteClass(id);
        return ResponseEntity.noContent().build();
    }

}
