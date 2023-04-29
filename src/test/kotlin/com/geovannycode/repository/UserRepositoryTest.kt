package com.geovannycode.repository

import com.geovannycode.TestDatabaseFactory
import com.geovannycode.di.bankingModule
import com.geovannycode.entities.user.UserEntity
import com.geovannycode.entities.user.UserTable
import com.geovannycode.models.User
import io.ktor.util.reflect.*
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.jetbrains.exposed.exceptions.ExposedSQLException
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
import java.time.ZoneOffset
import java.time.ZoneOffset.UTC
import java.util.UUID

internal class UserRepositoryTest: KoinTest {

    private lateinit var databaseFactory: TestDatabaseFactory
    private val userRepository by inject<UserRepository>()

    @JvmField
    @RegisterExtension
    val koinTestExtension = KoinTestExtension.create {
        modules(
            bankingModule
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
                password = "Ta1&tudol3lal54e",
                created = LocalDateTime.of(2023,1,1,1,0,0),
                lastUpdated = LocalDateTime.of(2023,1,2,1,0,0),
                accounts = listOf()
        )
        val current = userRepository.save(user)

        assertThat(current).isNotNull
        assertThat(transaction { UserEntity.find { UserTable.userId eq current.userId }.single() }).isNotNull
    }

    @Test
    fun `save updates existing user to database`() {
        val user = User(
            userId = UUID.randomUUID(),
            firstName = "Geovanny",
            lastName = "Mendoza",
            birthdate = LocalDate.of(2000,1,1),
            password = "Ta1&tudol3lal54e",
            created = LocalDateTime.of(2023,1,1,1,0,0),
            lastUpdated = LocalDateTime.of(2023,1,2,1,0,0),
            accounts = listOf()
        )
        val current = userRepository.save(user)

        val updatedUser = userRepository.save(
            current.copy(
                firstName = "Manuel"
            )
        )

        assertThat(updatedUser).isNotNull
        assertThat(transaction { UserEntity.all().count()}).isEqualTo(1)
        assertThat(transaction { UserEntity.find { UserTable.userId eq current.userId }.single().firstName}).isEqualTo("Manuel")
    }

    @Test
    fun `delete removes existing user from database`() {
        val user = User(
            userId = UUID.randomUUID(),
            firstName = "Geovanny",
            lastName = "Mendoza",
            birthdate = LocalDate.of(2000,1,1),
            password = "Ta1&tudol3lal54e",
            created = LocalDateTime.of(2023,1,1,1,0,0),
            lastUpdated = LocalDateTime.of(2023,1,2,1,0,0),
            accounts = listOf()
        )

        val userNew = userRepository.save(user)

        userRepository.delete(userNew)

        assertThat(transaction { UserEntity.find { UserTable.userId eq user.userId}.singleOrNull() }).isNull()
    }

    @Test
    fun `delete throws exception if try to delete user without id`() {
        val user = User(
            userId = UUID.randomUUID(),
            firstName = "Geovanny",
            lastName = "Mendoza",
            birthdate = LocalDate.of(2000,1,1),
            password = "Ta1&tudol3lal54e",
            created = LocalDateTime.of(2023,1,1,1,0,0),
            lastUpdated = LocalDateTime.of(2023,1,2,1,0,0),
            accounts = listOf()
        )

        assertThatThrownBy { userRepository.delete(user) }.instanceOf(IllegalStateException::class)
    }

    @Test
    fun `delete throws exception if try to delete user with not existing id`() {
        val user = User(
            userId = UUID.randomUUID(),
            firstName = "Geovanny",
            lastName = "Mendoza",
            birthdate = LocalDate.of(2000,1,1),
            password = "Ta1&tudol3lal54e",
            created = LocalDateTime.of(2023,1,1,1,0,0),
            lastUpdated = LocalDateTime.of(2023,1,2,1,0,0),
            accounts = listOf()
        )

        assertThatThrownBy { userRepository.delete(user) }.instanceOf(IllegalStateException::class)
    }

    @Test
    fun `findByUserId returns matching user`() {
        val user = User(
            userId = UUID.randomUUID(),
            firstName = "Geovanny",
            lastName = "Mendoza",
            birthdate = LocalDate.of(2000,1,1),
            password = "Ta1&tudol3lal54e",
            created = LocalDateTime.of(2023,1,1,1,0,0),
            lastUpdated = LocalDateTime.of(2023,1,2,1,0,0),
            accounts = listOf()
        )

        val userNew = userRepository.save(user)
        val current = userRepository.findByUserId(userNew.userId)
        assertThat(current).isNotNull
        current?.run{
            assertThat(this.userId).isEqualTo(userNew.userId)
            assertThat(this.firstName).isEqualTo(userNew.firstName)
            assertThat(this.lastName).isEqualTo(userNew.lastName)
            assertThat(this.created.toEpochSecond(UTC)).isEqualTo(
                userNew.created.toEpochSecond(ZoneOffset.UTC)
            )
            assertThat(this.lastUpdated.toEpochSecond(ZoneOffset.UTC)).isEqualTo(
                userNew.lastUpdated.toEpochSecond(
                    ZoneOffset.UTC
                )
            )
            assertThat(this.accounts).isEmpty()
        }
    }


    @Test
    fun `save fails if user with firstname, lastname and birthdate already exists`() {
        val user = User(
            userId = UUID.randomUUID(),
            firstName = "Geovanny",
            lastName = "Mendoza",
            birthdate = LocalDate.of(2000, 1, 1),
            password = "Ta1&tudol3lal54e",
            created = LocalDateTime.of(
                2022, 1, 1, 1, 0, 0
            ),
            lastUpdated = LocalDateTime.of(2022, 1, 2, 1, 0, 0),
            accounts = listOf()
        )
         userRepository.save(user)
        assertThatThrownBy {
            userRepository.save(
                user.copy(
                    userId = UUID.randomUUID()
                )
            )
        }.isInstanceOf(ExposedSQLException::class.java)
    }
}
