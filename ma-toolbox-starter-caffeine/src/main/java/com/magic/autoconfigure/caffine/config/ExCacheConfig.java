package com.magic.autoconfigure.caffine.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.AbstractAdvisingBeanPostProcessor;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author mzk
 * @description 类说明
 **/

@Configuration
@EnableCaching
@Getter
@Setter
@Slf4j
public class ExCacheConfig extends AbstractAdvisingBeanPostProcessor {

    @Getter
    @Setter
    public static class CacheSpec {
        private Long timeout = 1800L;
        private Integer init = 100;
        private Integer max = 1000;
    }

    private Map<String, CacheSpec> specs = new HashMap<>();

    private Map<String, Boolean> cacheConfig = new HashMap<>();


    @Autowired
    private ConfigurableEnvironment environment;

    @Bean(name = "caffeineCacheManager")
    @Primary
    public CacheManager cacheManager() {
        SimpleCacheManager manager = new SimpleCacheManager();
        List<CaffeineCache> caches =
                specs.entrySet().stream()
                        .map(entry -> buildCache(entry.getKey(), entry.getValue()))
                        .collect(Collectors.toList());
        manager.setCaches(caches);
        return manager;
    }

    private CaffeineCache buildCache(String name, CacheSpec cacheSpec) {
        log.info("Cache {} specified timeout of {}, min of {}, max of {}",
                name, cacheSpec.getTimeout(), cacheSpec.getInit(), cacheSpec.getMax());
        final Caffeine<Object, Object> caffeineBuilder
                = Caffeine.newBuilder()
                .expireAfterWrite(cacheSpec.getTimeout(), TimeUnit.SECONDS)
                .initialCapacity(cacheSpec.getInit())
                .maximumSize(cacheSpec.getMax());
        return new CaffeineCache(name, caffeineBuilder.build());
    }

    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Object object = super.postProcessAfterInitialization(bean, beanName);
        Class<?> targetClass = AopUtils.getTargetClass(bean);
        for (Method declaredMethod : targetClass.getDeclaredMethods()) {
            ExCaffeine config = declaredMethod.getAnnotation(ExCaffeine.class);
            if (config != null) {
                initCache(config);
            }
        }
        return object;
    }

    private String buildConfigName(String cacheName, String configName) {
        return cacheName + "#" + configName;
    }

    private CacheSpec getPropertyConfig(String cacheName) {
        String timeout = environment.getProperty(buildConfigName(cacheName, "caffeine_timeout"));
        String init = environment.getProperty(buildConfigName(cacheName, "caffeine_init"));
        String max = environment.getProperty(buildConfigName(cacheName, "caffeine_max"));
        if (StringUtils.isEmpty(timeout) && StringUtils.isEmpty(init) && StringUtils.isEmpty(max)) {
            return getCommunalPropertyConfig();
        }
        CacheSpec spec = new CacheSpec();
        spec.setTimeout(timeout == null ? 1800L : Long.parseLong(timeout));
        spec.setInit(init == null ? 100 : Integer.parseInt(init));
        spec.setMax(max == null ? 1000 : Integer.parseInt(max));
        return spec;
    }

    private CacheSpec getCommunalPropertyConfig() {
        String timeout = environment.getProperty("caffeine_timeout");
        String init = environment.getProperty("caffeine_init");
        String max = environment.getProperty("caffeine_max");
        CacheSpec spec = new CacheSpec();
        spec.setTimeout(timeout == null ? 1800L : Long.parseLong(timeout));
        spec.setInit(init == null ? 100 : Integer.parseInt(init));
        spec.setMax(max == null ? 1000 : Integer.parseInt(max));
        return spec;
    }

    private CacheSpec getInterfaceConfig(ExCaffeine caffeine) {
        long expire = caffeine.expire();
        int init = caffeine.init();
        int max = caffeine.max();
        CacheSpec spec = new CacheSpec();
        spec.setTimeout(expire);
        spec.setInit(init);
        spec.setMax(max);
        return spec;
    }


    private void initCache(ExCaffeine config) {
        for (String cacheName : config.cacheNames()) {
            boolean isLoad = config.loadProperty();
            if (isLoad && !this.cacheConfig.containsKey(cacheName)) {
                CacheSpec spec = getPropertyConfig(cacheName);
                this.specs.put(cacheName, spec);
                this.cacheConfig.put(cacheName, true);
            }
            if (!this.cacheConfig.containsKey(cacheName)) {
                CacheSpec spec = getInterfaceConfig(config);
                this.specs.put(cacheName, spec);
                this.cacheConfig.put(cacheName, true);
            }
            if (this.cacheConfig.containsKey(cacheName) && this.cacheConfig.get(cacheName)) {
                long expire = config.expire();
                int init = config.init();
                int max = config.max();
                if (isLoad) {
                    CacheSpec spec = getPropertyConfig(cacheName);
                    expire = spec.getTimeout();
                    init = spec.getInit();
                    max = spec.getMax();
                }
                CacheSpec cacheSpec = this.specs.get(cacheName);
                if (expire != cacheSpec.getTimeout() || init != cacheSpec.getInit() ||
                        max != cacheSpec.getMax()) {
                    throw new RuntimeException("启动失败！请确保@ExCaffeine注解下相同cacheName的配置一致");
                }
            }
        }
    }

}
