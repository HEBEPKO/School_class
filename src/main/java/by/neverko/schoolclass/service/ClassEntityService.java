package by.neverko.schoolclass.service;

import by.neverko.schoolclass.dto.ClassEntityDto;
import by.neverko.schoolclass.entity.ClassEntity;
import by.neverko.schoolclass.mapper.ClassMapper;
import by.neverko.schoolclass.repository.ClassRepository;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ClassEntityService {
    private final ClassRepository classRepository;
    private final ClassMapper classMapper;

    @Transactional(readOnly = true)
    public List<ClassEntityDto> getAllClass() {
        return classRepository.findAll().stream()
                .map(classMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public ClassEntityDto getClassId(Long id) {
        ClassEntity classes = classRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Такого класса с ID :" + id + " не существует"));
        return classMapper.toDto(classes);
    }

    public ClassEntityDto createdClass(@NotNull ClassEntityDto classEntityDto) {
        ClassEntity classEntity = new ClassEntity();
        classEntity.setName(classEntityDto.name());
        classEntity.setTimeCreated(classEntityDto.timeCreated());
        return classMapper.toDto(classRepository.save(classEntity));
    }

    public ClassEntityDto update(Long id, @NotNull ClassEntityDto classEntityDto) {
        ClassEntity existsClass = classRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Класс с ID :" + id + " не найден"));

        existsClass.setName(classEntityDto.name());

        ClassEntity updateClass = classRepository.save(existsClass);
        return classMapper.toDto(updateClass);
    }


    public void deleteClass(Long id) {
        if (!classRepository.existsById(id)) {
            throw new RuntimeException("Класс не найден с ID: " + id);
        }
        classRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public ClassEntityDto getClassName(String name) {
        ClassEntity classByName = classRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Такого класса с именем :" + "'" + name + "'" + " не существует"));
        return classMapper.toDto(classByName);
    }
}
