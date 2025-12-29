package by.neverko.schoolclass.service;

import by.neverko.schoolclass.dto.SubjectDto;
import by.neverko.schoolclass.entity.Subject;
import by.neverko.schoolclass.exception.ResourceNotFoundException;
import by.neverko.schoolclass.mapper.SubjectMapper;
import by.neverko.schoolclass.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubjectService {

    private static final Logger log = LoggerFactory.getLogger(SubjectService.class);
    private final SubjectRepository subjectRepository;
    private final SubjectMapper subjectMapper;

    public List<SubjectDto> getAllSubject() {
        return subjectRepository.findAll().stream()
                .map(subjectMapper::toDto)
                .toList();
    }

    public SubjectDto getSubjectById(Long id) {
        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Предмет не найден"));

        return subjectMapper.toDto(subject);
    }

    public SubjectDto createSubject(SubjectDto request) {
        log.info("Доходит");
        return subjectMapper.toDto(
                subjectRepository.save(
                        subjectMapper.toEntity(request)
                )
        );
    }

    public void deleteSubject(Long id) {
        if (!subjectRepository.existsById(id)) {
            log.info("Предмет не найден");
        }

        subjectRepository.deleteById(id);

    }
}
