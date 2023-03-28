package com.geovannycode.repository

import com.geovannycode.TestDatabaseFactory
import com.geovannycode.entities.account.AccountEntity
import com.geovannycode.entities.account.AccountTable
import com.geovannycode.entities.transaction.TransactionEntity
import com.geovannycode.entities.user.UserEntity
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

internal class AccountEntityTest {

    private lateinit var databaseFactory: TestDatabaseFactory

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
    fun `creating new account is possible`() {
        val user = transaction {
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

        val persistedAccount = transaction {
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
        assertThat(transaction { AccountEntity.findById(persistedAccount.id) }).isNotNull
    }

    @Test
    fun `delete account is possible`() {
        val user = transaction {
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

        val persistedAccount = transaction {
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
        transaction { persistedAccount.delete() }
        assertThat(transaction { AccountEntity.findById(persistedAccount.id) }).isNull()
        assertThat(transaction { UserEntity.findById(user.id) }).isNotNull
    }

    @Test
    fun `update account is possible`() {
        val user = transaction {
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

        val persistedAccount = transaction {
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
        transaction { persistedAccount.balance = 333.0 }
        assertThat(transaction { AccountEntity.findById(persistedAccount.id)?.balance }).isEqualTo(333.0)
    }

    @Test
    fun `find account is possible`() {
        val user = transaction {
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

        val persistedAccount = transaction {
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
        val current = transaction { AccountEntity.find { AccountTable.accountId eq persistedAccount.accountId }}
        assertThat(current).isNotNull
    }

    @Test
    fun `find account also loads origin transactions`() {
        val user = transaction {
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

        val account1 = transaction {
            AccountEntity.new {
                name = "My First Account"
                accountId = UUID.randomUUID()
                balance = 120.0
                dispo = -100.0
                limit = 100.0
                created = LocalDateTime.of(2023,1,1,1,9)
                lastUpdated = LocalDateTime.of(2023,1,1,2,9)
                userEntity = user
            }
        }

        val account2 = transaction {
            AccountEntity.new {
                name = "My Second Account"
                accountId = UUID.randomUUID()
                balance = 120.0
                dispo = -100.0
                limit = 100.0
                created = LocalDateTime.of(2023,1,1,1,9)
                lastUpdated = LocalDateTime.of(2023,1,1,2,9)
                userEntity = user
            }
        }

        transaction {
            TransactionEntity.new {
                transactionId = UUID.randomUUID()
                originEntity = account1
                targetEntity = account2
                amount = 123.0
                created = LocalDateTime.of(2023,1,3,2,9)
            }
        }

        val current = transaction { AccountEntity.find { AccountTable.accountId eq account1.accountId }.first() }
        assertThat(transaction{ current.originTransactions.count()}).isEqualTo(1)
        assertThat(transaction{ current.targetTransactions.count()}).isZero
    }

    @Test
    fun `find account also loads target transactions`() {
        val user = transaction {
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

        val account1 = transaction {
            AccountEntity.new {
                name = "My First Account"
                accountId = UUID.randomUUID()
                balance = 120.0
                dispo = -100.0
                limit = 100.0
                created = LocalDateTime.of(2023,1,1,1,9)
                lastUpdated = LocalDateTime.of(2023,1,1,2,9)
                userEntity = user
            }
        }

        val account2 = transaction {
            AccountEntity.new {
                name = "My Second Account"
                accountId = UUID.randomUUID()
                balance = 120.0
                dispo = -100.0
                limit = 100.0
                created = LocalDateTime.of(2023,1,1,1,9)
                lastUpdated = LocalDateTime.of(2023,1,1,2,9)
                userEntity = user
            }
        }

        transaction {
            TransactionEntity.new {
                transactionId = UUID.randomUUID()
                originEntity = account2
                targetEntity = account1
                amount = 100.0
                created = LocalDateTime.of(2023,2,2,2,9)
            }
        }

        val current = transaction { AccountEntity.find { AccountTable.accountId eq account1.accountId }.first() }
        assertThat(transaction{ current.targetTransactions.count()}).isEqualTo(1)
        assertThat(transaction{ current.originTransactions.count()}).isZero
    }
}