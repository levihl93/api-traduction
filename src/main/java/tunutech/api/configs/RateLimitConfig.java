package tunutech.api.configs;

import io.github.resilience4j.common.ratelimiter.configuration.RateLimiterConfigCustomizer;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;

import java.time.Duration;

@Configuration
@EnableCaching
@EnableRetry
public class RateLimitConfig {

    @Bean
    public RateLimiterConfigCustomizer translationRateLimiter() {
        return RateLimiterConfigCustomizer
                .of("translation-api", builder -> builder
                        .limitForPeriod(5)
                        .limitRefreshPeriod(Duration.ofSeconds(10))
                        .timeoutDuration(Duration.ofSeconds(5))
                );
    }

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("translations");
        cacheManager.setCaffeine(com.github.benmanes.caffeine.cache.Caffeine
                .newBuilder()
                .maximumSize(1000)
                .expireAfterWrite(10, java.util.concurrent.TimeUnit.MINUTES));
        return cacheManager;
    }
}
