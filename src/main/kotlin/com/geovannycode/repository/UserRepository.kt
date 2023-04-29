package com.geovannycode.repository

import com.geovannycode.models.User
import java.util.UUID

interface UserRepository {
    fun save(user: User): User
    fun delete(user: User)
    fun findByUserId(userId: UUID): User?
}

