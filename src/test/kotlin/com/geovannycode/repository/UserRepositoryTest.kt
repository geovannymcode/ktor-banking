package com.geovannycode.repository

import com.geovannycode.TestDatabaseFactory
import com.geovannycode.di.bankingAppModule
import com.geovannycode.entities.user.UserEntity
import com.geovannycode.entities.user.UserTable
import com.geovannycode.models.User
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.koin.test.KoinTest
import org.koin.test.inject
import org.koin.test.junit5.KoinTestExtension
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

internal class UserRepositoryTest: KoinTest {

    private lateinit var databaseFactory: TestDatabaseFactory
    private val userRepository by inject<UserRepository>()

    @JvmField
    @RegisterExtension
    val koinTestExtension = KoinTestExtension.create {
        modules(
            bankingAppModule
        )
    }

    @BeforeEach
    fun setupDatasource() {
        databaseFactory = TestDatabaseFactory()
        databaseFactory.connect()
    }

    @AfterEach
    fun tearDownDatasource() {
        databaseFactory.close()
    }

    @Test
    fun `save persists new user to database`() {
        val user = User(
                userId = UUID.randomUUID(),
                firstName = "Geovanny",
                lastName = "Mendoza",
                birthdate = LocalDate.of(2000,1,1),
                password = "test",
                created = LocalDateTime.of(2023,1,1,1,0,0),
                lastUpdated = LocalDateTime.of(2023,1,2,1,0,0),
                accounts = listOf()
        )
        val current = userRepository.save(user)

        assertThat(current).isNotNull
        assertThat(transaction { UserEntity.find { UserTable.userId eq current.userId }.single() }).isNotNull
    }
}