package by.neverko.schoolclass.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.client.RestTemplate;

import java.net.InetSocketAddress;
import java.net.Proxy;

@Configuration
public class TelegramConfig {

    @Value("${telegram.bot.proxy:false}")
    private boolean proxyEnabled;

    @Value("${telegram.bot.proxy.host:}")
    private String proxyHost;

    @Value("${telegram.bot.proxy.port:0}")
    private int proxyPort;

    @Bean
    public RestTemplate telegramRestTemplate() {
        if (proxyEnabled && proxyHost != null && !proxyHost.isEmpty() && proxyPort > 0) {
            SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));
            factory.setProxy(proxy);
            return new RestTemplate(factory);
        }
        return new RestTemplate();
    }

    @Bean
    public ThreadPoolTaskExecutor telegramTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(25);
        executor.setThreadNamePrefix("TelegramBot-");
        executor.initialize();
        return executor;
    }
}
