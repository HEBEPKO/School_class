package by.neverko.schoolclass.controller;

import by.neverko.schoolclass.security.SecurityUtils;
import by.neverko.schoolclass.service.NotificationService;
import by.neverko.schoolclass.service.TelegramBotService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/notification")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final TelegramBotService telegramBotService;
    private final SecurityUtils securityUtils;

    @PostMapping("/send/{studentId}")
    public ResponseEntity<String> sendPerformanceReport(@PathVariable Long studentId) {
        Long currentUserId = securityUtils.getCurrentUserId();

        if (currentUserId == null){
            return ResponseEntity.status(401).body("Пользователь не аутентифицирован");
        }

        // Проверяем, имеет ли текущий пользователь права на отправку отчетов
        if (!securityUtils.canSendPerformanceReport(currentUserId, studentId)) {
            return ResponseEntity.status(403).body("У вас нет прав для отправки отчетов по этому ученику");
        }

        try {
            // Генерируем отчет
            String report = notificationService.generatePerformanceReport(studentId);

            // Отправляем отчет родителям через Telegram
            boolean sent = notificationService.sendPerformanceReportParents(studentId);

            if (sent) {
                return ResponseEntity.ok("Отчёт успешно отправлен родителям");
            } else {
                return ResponseEntity.status(500).body("Не удалось отправить отчет родителям");
            }
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body("Ученик с ID" + studentId + "не найден");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Ошибка при генерации или отправке отчета: " + e.getMessage());
        }
    }
}
