package by.neverko.schoolclass.service;


import by.neverko.schoolclass.dto.UserDto;
import by.neverko.schoolclass.entity.ClassEntity;
import by.neverko.schoolclass.entity.Role;
import by.neverko.schoolclass.entity.User;
import by.neverko.schoolclass.mapper.UserMapper;
import by.neverko.schoolclass.repository.ClassRepository;
import by.neverko.schoolclass.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ClassRepository classRepository;
    private final UserMapper userMapper;

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
    public UserDto createUser(@NotNull UserDto userDto) {
        User user = new User();
        user.setRole(userDto.role());
        user.setName(userDto.name());
        user.setEmail(userDto.email());
        user.setPhone(userDto.phone());

        if (userDto.studentId() != null) {
            user.setStudent(userRepository.getReferenceById(userDto.studentId()));
        }
        if (userDto.classId() != null) {
            user.setClassEntity(userRepository.getReferenceById(userDto.classId()).getClassEntity());
        }
        return userMapper.toDto(userRepository.save(user));
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
}
