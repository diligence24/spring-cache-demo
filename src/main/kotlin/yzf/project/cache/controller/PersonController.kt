package yzf.project.cache.controller

import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.web.bind.annotation.*

/**
 * @author created by yzf on 29/01/2018
 */
@RestController
@RequestMapping("/api/person")
open class PersonController {

    @GetMapping("/name")
    @Cacheable(value = ["person-name"])
    open fun getByName(@RequestParam("name") name: String): Person {
        Thread.sleep(3000L)
        return Person(name = "yzf", age = 26, id = 1L)
    }

    @GetMapping("/id/{id}")
    @Cacheable(value = ["person-id"])
    open fun getById(@PathVariable id: Long): Person {
        Thread.sleep(3000L)
        return Person(1L, "yzf", 26)
    }

    @PutMapping("/update")
    @CacheEvict(value = ["person-id"], key = "#root.caches[0].name + ':' + #person.id")
    open fun updateById(@RequestBody person: Person): Boolean {
        return true
    }

    @DeleteMapping("/delete/{id}")
    @CacheEvict(value = ["person-id"])
    open fun deleteByName(@PathVariable id: Long): Boolean {
        return true
    }

    @PostMapping("/create")
    @CachePut(value = ["person-id"], key = "#root.caches[0].name + ':' + #person.id")
    open fun create(@RequestBody person: Person): Person {
        return Person(1L, "yzf", 26)
    }
}