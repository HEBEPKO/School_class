package by.neverko.schoolclass.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * Сервис для отправки сообщений через Telegram Bot API.

 * Основные улучшения:
 * - Исправлены ошибки компиляции
 * - Удалены неиспользуемые поля и методы
 * - Исправлены проблемы с типами
 * - Обновлены устаревшие аннотации
 */
@Service
@Slf4j
public class TelegramBotService {

    private static final int MAX_MESSAGE_LENGTH = 4096;
    private static final String TELEGRAM_API_URL = "https://api.telegram.org/bot%s/sendMessage";

    @Value("${telegram.bot.token}")
    private final String botToken;

    private final RestTemplate restTemplate;

    @Autowired
    public TelegramBotService(@Value("${telegram.bot.token}") String botToken,
                              RestTemplate restTemplate) {
        this.botToken = botToken;
        this.restTemplate = restTemplate;
    }

    /**
     * Синхронная отправка сообщения в Telegram
     */
    public void sendMessage(String chatId, String text) {
        sendMessage(chatId, text, ParseMode.MARKDOWN);
    }

    /**
     * Синхронная отправка сообщения в Telegram с указанием режима парсинга
     */
    public void sendMessage(String chatId, String text, ParseMode parseMode) {
        if (!validateInput(chatId, text)) {
            return;
        }

        // Разбиваем длинное сообщение на части
        if (text.length() > MAX_MESSAGE_LENGTH) {
            sendLongMessage(chatId, text, parseMode);
            return;
        }

        try {
            sendTelegramMessage(chatId, text, parseMode);
        } catch (Exception e) {
            log.error("Критическая ошибка при отправке сообщения в Telegram", e);
        }
    }

    /**
     * Валидация входных данных перед отправкой
     */
    private boolean validateInput(String chatId, String text) {
        if (botToken == null || botToken.trim().isEmpty()) {
            log.error("Telegram bot token не задан в конфигурации");
            return false;
        }

        if (chatId == null || chatId.trim().isEmpty()) {
            log.warn("Пропущена отправка: chatId пуст");
            return false;
        }

        if (text == null || text.trim().isEmpty()) {
            log.warn("Пропущена отправка: текст сообщения пуст");
            return false;
        }

        return true;
    }

    /**
     * Отправка длинного сообщения, разбитого на части
     */
    private void sendLongMessage(String chatId, String text, ParseMode parseMode) {
        int position = 0;
        while (position < text.length()) {
            int end = Math.min(position + MAX_MESSAGE_LENGTH, text.length());

            // Пытаемся разорвать сообщение на границе абзаца
            if (end < text.length()) {
                int lastBreak = text.lastIndexOf('\n', end);
                if (lastBreak > position) {
                    end = lastBreak + 1;
                }
            }

            String part = text.substring(position, end);
            try {
                sendTelegramMessage(chatId, part, parseMode);

                // Безопасная задержка с проверкой прерывания
                try {

                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    log.warn("Прервано ожидание между отправкой частей сообщения");
                    break;
                }
            } catch (Exception e) {
                log.error("Ошибка при отправке части сообщения", e);
            }

            position = end;
        }
    }

    /**
     * Отправка сообщения в Telegram с повторными попытками
     */
    @Retryable(
            retryFor = {HttpClientErrorException.TooManyRequests.class,
                    HttpServerErrorException.ServiceUnavailable.class},
            backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    private void sendTelegramMessage(String chatId, String text, ParseMode parseMode) {
        String url = String.format(TELEGRAM_API_URL, botToken);

        Map<String, Object> payload = new HashMap<>();
        payload.put("chat_id", chatId);
        payload.put("text", text);
        if (parseMode != null) {
            payload.put("parse_mode", parseMode.getMode());
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

        try {
            ResponseEntity<TelegramApiResponse> response = restTemplate.postForEntity(
                    url, request, TelegramApiResponse.class);

            handleTelegramResponse(response, chatId, text);
        } catch (RestClientException e) {
            log.error("Ошибка при отправке запроса к Telegram API", e);
            throw e;
        }
    }

    /**
     * Обработка ответа от Telegram API
     */
    private void handleTelegramResponse(ResponseEntity<TelegramApiResponse> response,
                                        String chatId, String text) {
        if (!response.hasBody()) {
            log.error("Пустой ответ от Telegram API");
            return;
        }

        TelegramApiResponse apiResponse = response.getBody();

        if (apiResponse == null || !apiResponse.isOk()) {
            String errorMessage = apiResponse != null ? apiResponse.getDescription() : "Неизвестная ошибка";
            log.error("Ошибка Telegram API (код {}): {}. Сообщение: {}",
                    response.getStatusCode(), errorMessage, text);

            // Специфическая обработка ошибок
            if (errorMessage != null && errorMessage.contains("bot was blocked by the user")) {
                log.warn("Бот заблокирован пользователем с chatId: {}", chatId);
            } else if (errorMessage != null && errorMessage.contains("retry after")) {
                // Telegram возвращает сообщение вида "Too Many Requests: retry after 10"
                int retryAfter = extractRetryAfter(errorMessage);
                log.warn("Превышен лимит запросов. Повтор через {} секунд", retryAfter);
            }
        } else {
            log.debug("Сообщение успешно отправлено в чат {}", chatId);
        }
    }

    /**
     * Извлечение времени ожидания из сообщения об ошибке
     */
    private int extractRetryAfter(String errorMessage) {
        try {
            String[] parts = errorMessage.split("retry after");
            if (parts.length > 1) {
                return Integer.parseInt(parts[1].trim());
            }
        } catch (Exception e) {
            log.debug("Не удалось извлечь время ожидания из сообщения: {}", errorMessage);
        }
        return 60; // Значение по умолчанию
    }

    /**
     * Режимы парсинга текста
     */
    @Getter
    public enum ParseMode {
        MARKDOWN("Markdown"),
        MARKDOWN_V2("MarkdownV2"),
        HTML("HTML");

        private final String mode;

        ParseMode(String mode) {
            this.mode = mode;
        }
    }

    /**
     * Модель для десериализации ответа Telegram API
     */
    @lombok.Data
    private static class TelegramApiResponse {
        private boolean ok;
        private int error_code;
        private String description;
    }
}