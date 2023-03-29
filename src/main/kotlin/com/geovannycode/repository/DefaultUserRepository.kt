package com.geovannycode.repository

import com.geovannycode.entities.user.UserEntity
import com.geovannycode.entities.user.UserTable
import com.geovannycode.models.User
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime

class DefaultUserRepository: UserRepository {
    override fun save(user: User): User = transaction {
        val currentDateTime = LocalDateTime.now()
        val existingUser = UserEntity.find {
            UserTable.userId eq user.userId }.firstOrNull()
        if (existingUser == null){
            val id = UserEntity.new {
                userId = user.userId
                firstName = user.firstName
                lastName = user.lastName
                birthdate = user.birthdate
                password = user.password
                created = currentDateTime
                lastUpdated = currentDateTime
            }.id
            user.copy(
                created = currentDateTime,
                lastUpdated = currentDateTime
            )
        }else{
            existingUser.userId = user.userId
            existingUser.firstName = user.firstName
            existingUser.lastName = user.lastName
            existingUser.birthdate = user.birthdate
            existingUser.password = user.password
            existingUser.created = currentDateTime
            existingUser.lastUpdated = currentDateTime
            user.copy(
                lastUpdated = currentDateTime
            )
        }
    }
}