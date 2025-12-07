package by.neverko.schoolclass.controller;

import by.neverko.schoolclass.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Controller
@RequestMapping("/telegram/webhook")
@RequiredArgsConstructor
public class TelegramWebhookController {

    private final UserRepository userRepository;

    @PostMapping
    public ResponseEntity<?> handleUpdate(@RequestBody Map<String, Object> update) {
        // Извлечь chat_id и текст
        // Если текст == "/start student123", то связать chat_id с учеником 123
        // Сохранить в user.telegramChatId
        return ResponseEntity.ok().build();
    }
}
