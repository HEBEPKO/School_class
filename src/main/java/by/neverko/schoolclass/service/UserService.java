package by.neverko.schoolclass.service;


import by.neverko.schoolclass.auth.dto.RegisterRequest;
import by.neverko.schoolclass.dto.CreateUserRequest;
import by.neverko.schoolclass.dto.UserDto;
import by.neverko.schoolclass.dto.UserResponse;
import by.neverko.schoolclass.entity.ClassEntity;
import by.neverko.schoolclass.entity.Role;
import by.neverko.schoolclass.entity.Subject;
import by.neverko.schoolclass.entity.User;
import by.neverko.schoolclass.exception.ResourceNotFoundException;
import by.neverko.schoolclass.mapper.UserMapper;
import by.neverko.schoolclass.repository.ClassRepository;
import by.neverko.schoolclass.repository.SubjectRepository;
import by.neverko.schoolclass.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final ClassRepository classRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final SubjectRepository subjectRepository;

    @Transactional(readOnly = true)
    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        return userMapper.toDto(user);
    }

    @Transactional(readOnly = true)
    public List<UserDto> getAllUser() {
        return userRepository.findAll().stream()
                .map(userMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<UserDto> getAllUserByRole(Role role) {
        if (role == null) {
            throw new IllegalArgumentException("Роль не найдена");
        }
        List<User> users = userRepository.findByRole(role);
        return users.stream()
                .map(userMapper::toDto)
                .toList();
    }

    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        User user = userMapper.toEntity(request);

        if (request.password() != null) {
            user.setPassword(passwordEncoder.encode(request.password()));
            log.info("Пороль добавлен");
        } else {
            log.info("Пароль не установлен");
        }

        if (request.role().equals(Role.STUDENT)){
            log.info("Для студента не устанавливается studentId");
        } else if (request.studentId() != null) {
            user.setStudent(userRepository.getReferenceById(request.studentId()));
            log.info("ID={} ученика добавлен", request.studentId());
        }

        if (request.classId() != null) {
            user.setClassEntity(classRepository.getReferenceById(request.classId()));
            log.info("ID={} класса добавлен", request.classId());
        } else {
            log.info("ID класса NULL");
        }
        User save = userRepository.save(user);

        return userMapper.toResponse(userRepository.save(save));
    }

    @Transactional
    public UserDto updateUser(Long id, @NotNull UserDto userDto) {
        User existtingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден с ID: " + id));

        existtingUser.setRole(userDto.role());
        existtingUser.setName(userDto.name());
        existtingUser.setEmail(userDto.email());
        existtingUser.setPhone(userDto.phone());

        if (userDto.studentId() != null) {
            User student = userRepository.getReferenceById(userDto.studentId());
            existtingUser.setStudent(student);
        } else {
            existtingUser.setStudent(null);
        }

        if (userDto.classId() != null) {
            ClassEntity classEntity = classRepository.getReferenceById(userDto.classId());
            existtingUser.setClassEntity(classEntity);
        } else {
            existtingUser.setClassEntity(null);
        }

        User updatedUser = userRepository.save(existtingUser);

        return userMapper.toDto(updatedUser);
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("Пользователь не найден с ID: " + id);
        }
        userRepository.deleteById(id);
    }

    public void registerUser(@NotNull RegisterRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            log.info("Пользователь с таким email={} уже существует", request.getEmail());
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .role(request.getRole())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        switch (request.getRole()) {
            case STUDENT -> {
                if (request.getClassId() == null) {
                    log.info("У ученика должен быть указан classId");
                }
                ClassEntity classEntity = classRepository.findById(request.getClassId())
                        .orElseThrow(() -> new ResourceNotFoundException("Класс с Id: " + request.getClassId() + " не найден"));
                user.setClassEntity(classEntity);
            }
            case PARENT -> {
                if (request.getStudentId() == null) {
                    throw new IllegalArgumentException("У родителя должен быть указан studentId");
                }
                User student = userRepository.findById(request.getStudentId())
                        .orElseThrow(() -> new ResourceNotFoundException("Ученик не найден"));
                if (student.getRole() != Role.STUDENT) {
                    log.info("Указанный пользователь name={} не является учеником", student.getName());
                }
                user.setStudent(student);

                user.setClassEntity(student.getClassEntity());
            }

            case TEACHER -> {
                if (request.getSubjectIds() == null || request.getSubjectIds().isEmpty()) {
                    log.info("У учителя должен быть хотя бы один предмет");
                }
                List<Subject> subjects = subjectRepository.findAllById(request.getSubjectIds());
                if (subjects.size() != request.getSubjectIds().size()) {
                    log.info("Один или несколько предметов не найдены");
                }
                user.setSubjects(subjects);
            }
            case CLASS_TEACHER ->{

                User classTeacher = userRepository.findClassTeacherByClassId(request.getClassId())
                        .orElseThrow(() -> new ResourceNotFoundException("Пользователь не является классным руководителем"));

                ClassEntity classEntity = classRepository.findById(request.getClassId())
                        .orElseThrow(() -> new ResourceNotFoundException("Класс с Id: " + request.getClassId() + " не найден"));
                user.setClassEntity(classTeacher.getClassEntity());
            }
            case ADMIN -> {}
            default -> throw new IllegalArgumentException("Неизвестная роль: " + request.getRole());
        }
        userRepository.save(user);
    }
}
