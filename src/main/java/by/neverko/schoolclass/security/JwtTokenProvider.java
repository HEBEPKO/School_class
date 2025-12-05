package by.neverko.schoolclass.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

/**
 * Предоставляет функциональность для работы с JWT токенами.
 * Использует современный API JJWT (0.12.0+).

 * Основные улучшения:
 * - Использование современного API JJWT без устаревших методов
 * - Добавление стандартных claims (iss, aud, jti)
 * - Использование Instant вместо Date
 * - Проверка длины секретного ключа
 * - Улучшенная обработка ошибок
 * - Добавление дополнительной информации в токен
 */

@Component
@Slf4j
public class JwtTokenProvider {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration-ms:86400000}")
    private long jwtExpirationMs;

    @Value("${app.jwt.issuer:school-class}")
    private String issuer;

    @Value("${app.jwt.audience:web-client}")
    private String audience;

    private SecretKey secretKey;

    @PostConstruct
    public void init() {
        if (jwtSecret.length() < 32) {
            log.warn("Секретный ключ короче 32 символов. Рекомендуется использовать ключ длиной не менее 32 символов для безопасности.");
        }

        this.jwtSecret = String.valueOf(Keys.hmacShaKeyFor(jwtSecret.getBytes()));
    }

    /**
     * Генерирует JWT токен для указанного пользователя
     *
     * @param userId ID пользователя
     * @return JWT токен в виде строки
     */

    public String generateToken(Long userId) {
        Instant now = Instant.now();
        Instant expiryDate = now.plus(jwtExpirationMs, ChronoUnit.MILLIS);
        String tokenId = UUID.randomUUID().toString();

        return Jwts.builder()
                .header()
                    .type("JWT") // Указываем тип токена
                    .and()
                .claims()
                    .issuer(issuer) // Издатель токена
                    .subject(userId.toString()) // Субъект (ID пользователя)
                    .audience().add(audience).and() // Аудитория
                    .issuedAt(Date.from(now)) // Время выдачи
                    .expiration(Date.from(expiryDate)) // Время окончания
                    .id(tokenId) // Уникальный идентификатор токена
                    .and()
                .signWith(secretKey) // Алгоритм определяется автоматически по типу ключа
                .compact();
    }
    /**
     * Извлекает ID пользователя из JWT токена
     *
     * @param token JWT токен
     * @return ID пользователя
     * @throws IllegalArgumentException если токен недействителен или поврежден
     */
    public Long getUserIdFromJWT(String token) {
        try {
            Jws<Claims> claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);

            return Long.parseLong(claims.getPayload().getSubject());
        } catch (SecurityException | IllegalArgumentException e) {
            log.error("Ошибка при извлечении ID пользователя из JWT: {}", e.getMessage());
            throw new IllegalArgumentException("Некорректный JWT токен");
        }
    }

    /**
     * Валидирует JWT токен
     *
     * @param token JWT токен
     * @return true если токен валиден, иначе false
     */

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | io.jsonwebtoken.MalformedJwtException e) {
            log.error("Некорректный JWT токен: {}", e.getMessage());
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            log.error("Истек срок действия JWT токена: {}", e.getMessage());
        } catch (io.jsonwebtoken.UnsupportedJwtException e) {
            log.error("JWT токен не поддерживается: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT токен пуст: {}", e.getMessage());
        }
        return false;
    }

    /**
     * Проверяет, истек ли срок действия токена
     *
     * @param token JWT токен
     * @return true если срок действия истек, иначе false
     */

    public boolean isTokenExpired(String token) {
        try {
            Jws<Claims> claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);
            return claims.getPayload().getExpiration().before(new Date());
        } catch (Exception e) {
            return true; // Если токен недействителен, считаем его просроченным
        }
    }

}
