package com.geovannycode.repository

import com.geovannycode.models.User
import java.util.*

interface UserRepository {
    fun save(user: User): User
    fun delete(user: User): Unit
    fun findByUserId(userId: UUID): User?
}

