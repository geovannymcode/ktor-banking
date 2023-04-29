package com.geovannycode.repository

import com.geovannycode.TestDatabaseFactory
import com.geovannycode.di.bankingModule
import com.geovannycode.entities.account.AccountEntity
import com.geovannycode.entities.account.AccountTable
import com.geovannycode.models.Account
import com.geovannycode.models.User
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.koin.test.KoinTest
import org.koin.test.inject
import org.koin.test.junit5.KoinTestExtension
import java.time.LocalDate
import java.util.UUID

class AccountRepositoryTest : KoinTest {

    private lateinit var databaseFactory: TestDatabaseFactory
    private val accountRepository by inject<AccountRepository>()
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
    fun `save persists new account to database`() {
        val user = User(
            userId = UUID.randomUUID(),
            firstName = "Geovanny",
            lastName = "Mendoza",
            birthdate = LocalDate.of(2002, 1, 1),
            password = "Ta1&tudol3lal54e",
            accounts = listOf()
        )
        val persistedUser = userRepository.save(user)

        val account = Account(
            name = "My account",
            accountId = UUID.randomUUID(),
            balance = 120.0,
            dispo = -1000.0,
            limit = 1000.0,
        )
        val actual = accountRepository.saveForUser(persistedUser, account)

        assertThat(actual).isNotNull
        assertThat(transaction { AccountEntity.find { AccountTable.accountId eq actual.accountId }.single()}).isNotNull
    }

    @Test
    fun `save throws exception if user is not available in database`() {
        val user = User(
            userId = UUID.randomUUID(),
            firstName = "Geovanny",
            lastName = "Mendoza",
            birthdate = LocalDate.of(2002, 1, 1),
            password = "Ta1&tudol3lal54e",
            accounts = listOf()
        )

        val account = Account(
            name = "My account",
            accountId = UUID.randomUUID(),
            balance = 120.0,
            dispo = -1000.0,
            limit = 1000.0,
        )
        assertThatThrownBy { accountRepository.saveForUser(user, account) }.isInstanceOf(
            IllegalStateException::class.java
        )
    }

    @Test
    fun `save updates existing account to database`() {
        val user = User(
            userId = UUID.randomUUID(),
            firstName = "Geovanny",
            lastName = "Mendoza",
            birthdate = LocalDate.of(2002, 1, 1),
            password = "Ta1&tudol3lal54e",
            accounts = listOf()
        )
        val persistedUser = userRepository.save(user)

        val account = Account(
            name = "My account",
            accountId = UUID.randomUUID(),
            balance = 120.0,
            dispo = -1000.0,
            limit = 1000.0,
        )
        val persistedAccount = accountRepository.saveForUser(persistedUser, account)

        val updatedAccount = accountRepository.saveForUser(
            persistedUser, persistedAccount.copy(
                name = "My other account"
            )
        )

        assertThat(updatedAccount).isNotNull
        assertThat(transaction { AccountEntity.all().count() }).isEqualTo(1)
        assertThat(transaction {
            AccountEntity.find { AccountTable.accountId eq updatedAccount.accountId }.single().name
        })
            .isEqualTo("My other account")
    }

    @Test
    fun `delete removes account user relation from database`() {
        val user = User(
            userId = UUID.randomUUID(),
            firstName = "John",
            lastName = "Doe",
            birthdate = LocalDate.of(2000, 1, 1),
            password = "Ta1&tudol3lal54e",
            accounts = listOf()
        )
        val persistedUser = userRepository.save(user)

        val account = Account(
            name = "My account",
            accountId = UUID.randomUUID(),
            balance = 120.0,
            dispo = -1000.0,
            limit = 1000.0,
        )
        val persistedAccount = accountRepository.saveForUser(persistedUser, account)

        accountRepository.delete(persistedAccount)

        assertThat(transaction { AccountEntity.all().count() }).isEqualTo(1)
        assertThat(transaction {
            AccountEntity.find { AccountTable.accountId eq persistedAccount.accountId }.single().userEntity
        }).isNull()
    }

    @Test
    fun `delete throws exception if account not exists in database`() {
        val user = User(
            userId = UUID.randomUUID(),
            firstName = "Geovanny",
            lastName = "Mendoza",
            birthdate = LocalDate.of(2002, 1, 1),
            password = "Ta1&tudol3lal54e",
            accounts = listOf()
        )
        userRepository.save(user)

        val account = Account(
            name = "My account",
            accountId = UUID.randomUUID(),
            balance = 120.0,
            dispo = -1000.0,
            limit = 1000.0,
        )

        assertThatThrownBy {
            accountRepository.delete(account)
        }.isInstanceOf(IllegalStateException::class.java)
    }
}