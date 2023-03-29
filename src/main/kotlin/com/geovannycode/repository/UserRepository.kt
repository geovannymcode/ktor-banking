package com.geovannycode.repository

import com.geovannycode.models.User

interface UserRepository {
    fun save(user: User): User
}

