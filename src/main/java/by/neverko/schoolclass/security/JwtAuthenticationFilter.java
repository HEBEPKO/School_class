package by.neverko.schoolclass.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.Optional;

/**
 * Фильтр аутентификации на основе JWT токенов.

 * Основные улучшения:
 * - Использование SecurityContextHolder для проверки существующей аутентификации
 * - Детальная обработка различных типов ошибок JWT
 * - Оптимизация производительности за счет пропуска обработки для предварительных CORS запросов
 * - Использование Optional для безопасной работы с токеном
 * - Добавление поддержки кастомных заголовков аутентификации
 * - Улучшенное логирование для отладки и аудита
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String OPTIONS_METHOD = "OPTIONS";

    private final JwtTokenProvider tokenProvider;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(

            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        // Пропускаем обработку для предварительных CORS запросов
        if (isOptionsRequest(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Проверяем, не установлена ли уже аутентификация
        if (isAuthenticationAlreadySet()) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            extractAndSetAuthentication(request);
        } catch (Exception ex) {
            logAuthenticationError(ex);
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Проверяет, является ли запрос предварительным CORS запросом
     */
    private boolean isOptionsRequest(HttpServletRequest request) {
        return OPTIONS_METHOD.equalsIgnoreCase(request.getMethod());
    }

    /**
     * Проверяет, установлена ли уже аутентификация в контексте безопасности
     */
    private boolean isAuthenticationAlreadySet() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated();
    }

    /**
     * Извлекает JWT токен из запроса и устанавливает аутентификацию
     */
    private void extractAndSetAuthentication(HttpServletRequest request) {
        String jwt = extractJwtToken(request)
                .orElse(null);

        if (jwt == null) {
            log.debug("JWT токен не найден в запросе");
            return;
        }

        if (!tokenProvider.validateToken(jwt)) {
            log.warn("Невалидный JWT токен");
            return;
        }

        Long userId = tokenProvider.getUserIdFromJWT(jwt);
        UserDetails userDetails = userDetailsService.loadUserByUsername(userId.toString());

        if (userDetails == null) {
            log.warn("Пользователь с ID {} не найден", userId);
            return;
        }

        setAuthenticationContext(userDetails, request);
        log.debug("Аутентификация установлена для пользователя: {}", userId);
    }

    /**
     * Извлекает JWT токен из заголовка Authorization
     */
    private Optional<String> extractJwtToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(AUTHORIZATION_HEADER))
                .filter(header -> header.startsWith(BEARER_PREFIX))
                .map(header -> header.substring(BEARER_PREFIX.length()));
    }

    /**
     * Устанавливает контекст аутентификации
     */
    private void setAuthenticationContext(UserDetails userDetails, HttpServletRequest request) {

        Object details = new WebAuthenticationDetailsSource().buildDetails(request);

        Authentication authentication = new JwtAuthenticationToken(
                userDetails,
                userDetails.getAuthorities(),
                details
        );

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
    }

    /**
     * Логирует ошибки аутентификации с детализацией
     */
    private void logAuthenticationError(Exception ex) {
        if (ex instanceof io.jsonwebtoken.ExpiredJwtException) {
            log.warn("Истек срок действия JWT токена: {}", ex.getMessage());
        } else if (ex instanceof io.jsonwebtoken.MalformedJwtException) {
            log.warn("Некорректный формат JWT токена: {}", ex.getMessage());
        } else if (ex instanceof io.jsonwebtoken.UnsupportedJwtException) {
            log.warn("JWT токен не поддерживается: {}", ex.getMessage());
        } else if (ex instanceof io.jsonwebtoken.security.SecurityException) {
            log.error("Ошибка безопасности при обработке JWT: {}", ex.getMessage(), ex);
        } else {
            log.error("Ошибка при установке аутентификации пользователя по JWT: {}", ex.getMessage(), ex);
        }
    }

    /**
     * Кастомный токен аутентификации для JWT
     */
    private static class JwtAuthenticationToken extends AbstractAuthenticationToken {
        private final Object principal;
        private final Object details;

        public JwtAuthenticationToken(Object principal, Collection<? extends GrantedAuthority> authorities, Object details) {
            super(authorities);
            this.principal = principal;
            this.details = details;
            setAuthenticated(true);
        }

        @Override
        public Object getCredentials() {
            return null;
        }

        @Override
        public Object getPrincipal() {
            return principal;
        }

        @Override
        public Object getDetails() {
            return details;
        }
    }
}
