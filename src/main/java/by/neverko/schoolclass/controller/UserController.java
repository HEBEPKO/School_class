package by.neverko.schoolclass.controller;

import by.neverko.schoolclass.dto.UserDto;
import by.neverko.schoolclass.entity.Grade;
import by.neverko.schoolclass.entity.Role;
import by.neverko.schoolclass.entity.User;
import by.neverko.schoolclass.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/user")
    public ResponseEntity<List<UserDto>> getForUsers() {

        return ResponseEntity.ok(userService.getAllUser());
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<UserDto> getUser(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PostMapping("/user/save")
    public ResponseEntity<UserDto> addUser(@RequestBody UserDto userDto) {
        UserDto savedUser = userService.createUser(userDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDto> userUpdate(@PathVariable Long id, @RequestBody UserDto userDto) {
        UserDto updatedUser = userService.updateUser(id, userDto);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/user/role/{role}")
    public ResponseEntity<List<UserDto>> getForUsersRole(
            @PathVariable Role role
            ) {
        return ResponseEntity.ok(userService.getAllUserByRole(role));
    }
}
