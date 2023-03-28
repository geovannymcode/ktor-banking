package com.geovannycode.repository

import com.geovannycode.TestDatabaseFactory
import com.geovannycode.entities.account.AccountEntity
import com.geovannycode.entities.user.UserEntity
import com.geovannycode.entities.user.UserTable
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

internal class UserEntityTest {

    private lateinit var databaseFactory: TestDatabaseFactory

    @BeforeEach
    fun setupDatasource(){
        databaseFactory = TestDatabaseFactory()
        databaseFactory.connect()
    }

    @AfterEach
    fun tearDownDatasource(){
        databaseFactory.close()
    }

    @Test
    fun `creating new user is possible`(){
        val persistedUser = transaction {
            UserEntity.new {
                userId = UUID.randomUUID()
                firstName = "Geovanny"
                lastName = "Mendoza"
                birthdate = LocalDate.of(2000,1,1)
                password = "test"
                created = LocalDateTime.of(2023,1,1,1,9)
                lastUpdated = LocalDateTime.of(2023,1,1,2,9)
            }
        }
        assertThat(transaction{UserEntity.findById(persistedUser.id)}).isNotNull
    }

    @Test
    fun `delete user is possible`() {
        val persistedUser = transaction {
            UserEntity.new {
                userId = UUID.randomUUID()
                firstName = "Geovanny"
                lastName = "Mendoza"
                birthdate = LocalDate.of(2000, 1, 1)
                password = "test"
                created = LocalDateTime.of(2023, 1, 1, 1, 9)
                lastUpdated = LocalDateTime.of(2023, 1, 1, 2, 9)
            }
        }
        transaction { persistedUser.delete() }
        assertThat(transaction { UserEntity.findById(persistedUser.id) }).isNull()
    }

    @Test
    fun `edit user is possible`(){
        val persistedUser = transaction {
            UserEntity.new {
                userId = UUID.randomUUID()
                firstName = "Geovanny"
                lastName = "Mendoza"
                birthdate = LocalDate.of(2000,1,1)
                password = "test"
                created = LocalDateTime.of(2023,1,1,1,9)
                lastUpdated = LocalDateTime.of(2023,1,1,2,9)
            }
        }
        transaction { persistedUser.firstName = "Manuel" }
        assertThat(transaction{UserEntity.findById(persistedUser.id)?.firstName}).isEqualTo("Manuel")
    }

    @Test
    fun `find user is possible`() {
        transaction {
            UserEntity.new {
                userId = UUID.randomUUID()
                firstName = "Geovanny"
                lastName = "Mendoza"
                birthdate = LocalDate.of(2000, 1, 1)
                password = "test"
                created = LocalDateTime.of(2023, 1, 1, 1, 9)
                lastUpdated = LocalDateTime.of(2023, 1, 1, 2, 9)
            }
        }
        val current = transaction { UserEntity.find { UserTable.firstName eq "Geovanny" }.first() }
        assertThat(current).isNotNull
        assertThat(transaction { current.accounts.count()}).isZero
    }

    @Test
    fun `find user also loads accounts`() {
        // given
        val user = transaction {
            UserEntity.new {
                userId = UUID.randomUUID()
                firstName = "Geovanny"
                lastName = "Mendoza"
                birthdate = LocalDate.of(2000, 1, 1)
                password = "test"
                created = LocalDateTime.of(2023, 1, 1, 1, 9)
                lastUpdated = LocalDateTime.of(2023, 1, 1, 2, 9)
            }
        }
        transaction {
            AccountEntity.new {
                name = "My Account"
                accountId = UUID.randomUUID()
                balance = 120.0
                dispo = -100.0
                limit = 100.0
                created = LocalDateTime.of(2023,1,1,1,9)
                lastUpdated = LocalDateTime.of(2023,1,1,2,9)
                userEntity = user
            }
        }
        val current = transaction { UserEntity.find { UserTable.firstName eq "Geovanny" }.first() }
        assertThat(current).isNotNull
        assertThat(transaction { current.accounts.count()}).isEqualTo(1)
    }

}