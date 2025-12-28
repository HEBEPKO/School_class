package by.neverko.schoolclass.auth;


import by.neverko.schoolclass.dto.CreateUserRequest;
import by.neverko.schoolclass.dto.UserDto;
import by.neverko.schoolclass.dto.UserResponse;
import by.neverko.schoolclass.entity.Role;
import by.neverko.schoolclass.repository.UserRepository;
import by.neverko.schoolclass.service.UserService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class InitialSetupController {

    private static final Logger log = LoggerFactory.getLogger(InitialSetupController.class);
    private final UserService userService;
    private final UserRepository userRepository;

    @PostConstruct
    public void checkIfAdminExists() {
        if (userRepository.existsByRole(Role.ADMIN)) {
            log.info("Первоначальная настройка не требуется");
        }
    }

    @PostMapping("/api/setup/admin")
    public ResponseEntity<UserResponse> createInitialAdmin(@RequestBody CreateUserRequest request) {
        if (userRepository.existsByRole(Role.ADMIN)) {
            return ResponseEntity.badRequest().build();
        }

        CreateUserRequest adminRequest = new CreateUserRequest(
                Role.ADMIN,
                request.name(),
                request.email(),
                request.phone(),
                request.password(),
                null,
                null
        );
        return ResponseEntity.status(HttpStatus.OK).body(userService.createUser(adminRequest));
    }
}
