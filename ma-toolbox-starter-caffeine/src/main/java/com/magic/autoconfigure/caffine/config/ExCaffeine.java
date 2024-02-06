package com.magic.autoconfigure.caffine.config;


import org.springframework.cache.annotation.CacheConfig;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;
import java.util.concurrent.Callable;

@Target({ElementType.TYPE, ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ExCaffeine {

    /**
     * Alias for {@link #cacheNames}.
     */
    @AliasFor("cacheNames")
    String[] value() default {};

    /**
     * Names of the caches in which method invocation results are stored.
     * <p>Names may be used to determine the target cache (or caches), matching
     * the qualifier value or bean name of a specific bean definition.
     *
     * @see #value
     * @see CacheConfig#cacheNames
     * @since 4.2
     */
    @AliasFor("value")
    String[] cacheNames() default {};

    /**
     * 指定缓存的 key,支持 SpEL 表达式，比如 key = "#userId" 表示拿参数中的 userId 为 key
     *
     * @return
     */
    String key() default "";

    /**
     * The bean name of the custom {@link org.springframework.cache.interceptor.KeyGenerator}
     * to use.
     * <p>Mutually exclusive with the {@link #key} attribute.
     *
     * @see CacheConfig#keyGenerator
     */
    String keyGenerator() default "";

    /**
     * The bean name of the custom {@link org.springframework.cache.CacheManager} to use to
     * create a default {@link org.springframework.cache.interceptor.CacheResolver} if none
     * is set already.
     * <p>Mutually exclusive with the {@link #cacheResolver}  attribute.
     *
     * @see org.springframework.cache.interceptor.SimpleCacheResolver
     * @see CacheConfig#cacheManager
     */
    String cacheManager() default "";

    /**
     * The bean name of the custom {@link org.springframework.cache.interceptor.CacheResolver}
     * to use.
     *
     * @see CacheConfig#cacheResolver
     */
    String cacheResolver() default "";


    /**
     * 表示是否需要缓存，默认为空，表示所有情况都会缓存。通过SpEL表达式来指定，
     * 若condition的值为true则会缓存，若为false则不会缓存，
     * 如@Cacheable(value=“Dept”,key="‘deptno_’+# deptno “,condition=”#deptno<=40")
     * @return
     */
    String condition() default "";

    /**
     * 当调用这个方法时，会先在本地缓存中查找是否已经缓存了结果，如果有，则直接返回缓存中的结果；
     * 如果没有，则执行方法体，并将方法的返回值缓存到本地缓存中，供下次使用。如果方法的返回值为 null，则不会将结果缓存。
     *
     * @return
     */
    String unless() default "";

    /**
     * Synchronize the invocation of the underlying method if several threads are
     * attempting to load a value for the same key. The synchronization leads to
     * a couple of limitations:
     * <ol>
     * <li>{@link #unless()} is not supported</li>
     * <li>Only one cache may be specified</li>
     * <li>No other cache-related operation can be combined</li>
     * </ol>
     * This is effectively a hint and the actual cache provider that you are
     * using may not support it in a synchronized fashion. Check your provider
     * documentation for more details on the actual semantics.
     *
     * @see org.springframework.cache.Cache#get(Object, Callable)
     * @since 4.3
     */
    boolean sync() default false;

    /**
     * 是否从 yml 读取配置
     * @return
     */
    boolean loadProperty() default false;

    /**
     * 过期时间，默认 1800 秒
     *
     * @return
     */
    long expire() default 1800;

    /**
     * Sets the minimum total size for the internal data structures. Providing a large enough estimate
     * at construction time avoids the need for expensive resizing operations later, but setting this
     * value unnecessarily high wastes memory.
     *
     * @return
     */
    int init() default 100;

    /**
     * Specifies the maximum number of entries the cache may contain. Note that the cache <b>may evict
     * an entry before this limit is exceeded or temporarily exceed the threshold while evicting</b>.
     * As the cache size grows close to the maximum, the cache evicts entries that are less likely to
     * be used again. For example, the cache may evict an entry because it hasn't been used recently
     * or very often.
     * <p>
     * When {@code size} is zero, elements will be evicted immediately after being loaded into the
     * cache. This can be useful in testing, or to disable caching temporarily without a code change.
     * As eviction is scheduled on the configured , tests may instead prefer
     * to configure the cache to execute tasks directly on the same thread.
     * <p>
     * This feature cannot be used in conjunction with .
     */
    int max() default 1000;
}
