package yzf.project.cache.redis

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.PropertyAccessor
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import org.springframework.beans.factory.annotation.Value
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.*
import org.springframework.cache.interceptor.KeyGenerator
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.cache.RedisCacheManager
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer
import yzf.project.cache.constants.RedisCacheNames

/**
 * @author created by yzf on 29/01/2018
 */
@Configuration
@EnableCaching
open class RedisConfiguration : CachingConfigurerSupport() {


    @Bean
    open fun redisConnectionFactory(@Value("\${spring.redis.host}") host: String,
                                    @Value("\${spring.redis.port}") port: Int,
                                    @Value("\${spring.redis.password}") password: String,
                                    @Value("\${spring.redis.database}") dbIndex: Int) : JedisConnectionFactory {
        val jedisConnectionFactory = JedisConnectionFactory()
        jedisConnectionFactory.hostName = host
        jedisConnectionFactory.port = port
        jedisConnectionFactory.database = dbIndex
        jedisConnectionFactory.password = password
        return jedisConnectionFactory
    }

    @Bean
    open fun redisTemplate(redisConnectionFactory: RedisConnectionFactory) : RedisTemplate<String, String> {
        val template = RedisTemplate<String, String>()

        setSerializer(template)
        template.connectionFactory = redisConnectionFactory
        template.afterPropertiesSet()

        return template
    }

    private fun setSerializer(template: RedisTemplate<String, String>) {
        val jackson2JsonRedisSerializer = Jackson2JsonRedisSerializer(Any::class.java)

        val objectMapper = ObjectMapper()
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        objectMapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true)
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY)
        objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL)

        jackson2JsonRedisSerializer.setObjectMapper(objectMapper)

        template.keySerializer = StringRedisSerializer()
        template.valueSerializer = jackson2JsonRedisSerializer
    }

    @Bean
    open fun cacheManager(redisTemplate: RedisTemplate<String, String>): CacheManager {
        val redisCacheManager = RedisCacheManager(redisTemplate)
        val cacheNameAndTTL = mutableMapOf<String, Long>()
        cacheNameAndTTL[RedisCacheNames.PERSON_ID] = 10
        cacheNameAndTTL[RedisCacheNames.PERSON_NAME] = 20
        redisCacheManager.setExpires(cacheNameAndTTL)
        redisCacheManager.afterPropertiesSet()
        return redisCacheManager
    }

    /**
     * key generator strategy:  cacheName + params
     */
    @Bean
    override fun keyGenerator(): KeyGenerator {
        return KeyGenerator { target, method, params ->
            run {
                var key = ""
                val cacheable = method.getAnnotation(Cacheable::class.java)
                if (cacheable != null) {
                    key += cacheable.value[0]
                }
                val cachePut = method.getAnnotation(CachePut::class.java)
                if (cachePut != null) {
                    key += cachePut.value[0]
                }
                val cacheEvict = method.getAnnotation(CacheEvict::class.java)
                if (cacheEvict != null) {
                    key += cacheEvict.value[0]
                }

                params.forEach {
                    key += ":"
                    key += it.toString()
                }
                return@run key
            }
        }
    }
}