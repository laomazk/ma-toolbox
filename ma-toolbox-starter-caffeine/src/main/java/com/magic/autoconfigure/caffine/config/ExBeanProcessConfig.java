package com.magic.autoconfigure.caffine.config;

import org.springframework.aop.framework.AbstractAdvisingBeanPostProcessor;
import org.springframework.beans.BeansException;
import org.springframework.cache.annotation.AnnotationCacheOperationSource;
import org.springframework.context.annotation.Configuration;


/**
 * @author mzk
 * @description 类说明
 **/

@Configuration
public class ExBeanProcessConfig extends AbstractAdvisingBeanPostProcessor {


    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if ("cacheOperationSource".equals(beanName)) {
            return new AnnotationCacheOperationSource(new SpringCacheAnnotationParser());
        }
        return bean;
    }

}
