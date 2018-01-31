package yzf.project.cache.controller

import yzf.project.cache.annotation.NoArg

/**
 * @author created by yzf on 29/01/2018
 */
@NoArg
open class Person (
        val id: Long,
        val name: String,
        val age: Int
)