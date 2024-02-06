package com.magic.autoconfigure.caffine;

import com.magic.autoconfigure.caffine.config.ExBeanProcessConfig;
import com.magic.autoconfigure.caffine.config.ExCacheConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({ExCacheConfig.class, ExBeanProcessConfig.class})
public class CaffeineAutoConfiguration {
}
