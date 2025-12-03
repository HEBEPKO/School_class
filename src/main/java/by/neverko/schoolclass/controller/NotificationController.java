package by.neverko.schoolclass.controller;

import by.neverko.schoolclass.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notification")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping("/send/{studentId}")
    public ResponseEntity<String> sendPerformanceReport(
            @PathVariable Long studentId,
            @RequestHeader("X-User-Id") Long currentUserId
    ) {
        // Реализовать проверку: currentUserId — учитель/классный руководитель
        String report = notificationService.generatePerformanceReport(studentId);

        /*
         TODO: отправить в Telegram/Viber
         telegramBotService.sendMessageToParents(studentId, report);
        */

        return ResponseEntity.ok("Отчёт отправлен родителям");
    }


}
