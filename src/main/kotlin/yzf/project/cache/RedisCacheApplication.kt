package yzf.project.cache

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

/**
 * @author created by yzf on 29/01/2018
 */
@SpringBootApplication
open class RedisCacheApplication

fun main(vararg args: String) {
    SpringApplication.run(RedisCacheApplication::class.java, *args)
}