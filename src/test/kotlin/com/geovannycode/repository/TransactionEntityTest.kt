package com.geovannycode.repository

import com.geovannycode.TestDatabaseFactory
import com.geovannycode.entities.account.AccountEntity
import com.geovannycode.entities.transaction.TransactionEntity
import com.geovannycode.entities.transaction.TransactionTable
import com.geovannycode.entities.user.UserEntity
import org.assertj.core.api.Assertions
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

internal class TransactionEntityTest {
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
    fun `creating new transaction is possible`() {
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

        val persistedTransaction = transaction {
            TransactionEntity.new {
                transactionId = UUID.randomUUID()
                originEntity = account1
                targetEntity = account2
                amount = 123.0
                created = LocalDateTime.of(2023,1,3,2,9)
            }
        }
        Assertions.assertThat(transaction {TransactionEntity.findById(persistedTransaction.id)}).isNotNull
    }

    @Test
    fun `delete transaction is possible`() {
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

        val persistedTransaction = transaction {
            TransactionEntity.new {
                transactionId = UUID.randomUUID()
                originEntity = account1
                targetEntity = account2
                amount = 123.0
                created = LocalDateTime.of(2023,1,3,2,9)
            }
        }
        transaction {  persistedTransaction.delete()}
        Assertions.assertThat(transaction {TransactionEntity.findById(persistedTransaction.id)}).isNull()
    }

    @Test
    fun `update transaction is possible`() {
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

        val persistedTransaction = transaction {
            TransactionEntity.new {
                transactionId = UUID.randomUUID()
                originEntity = account1
                targetEntity = account2
                amount = 123.0
                created = LocalDateTime.of(2023,1,3,2,9)
            }
        }
        transaction {  persistedTransaction.amount = 200.0}
        Assertions.assertThat(transaction {TransactionEntity.findById(persistedTransaction.id)?.amount}).isEqualTo(200.0)
    }

    @Test
    fun `finding transaction is possible`() {
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

        val persistedTransaction = transaction {
            TransactionEntity.new {
                transactionId = UUID.randomUUID()
                originEntity = account1
                targetEntity = account2
                amount = 123.0
                created = LocalDateTime.of(2023,1,3,2,9)
            }
        }
        val current= transaction {  TransactionEntity.find { TransactionTable.transactionId eq persistedTransaction.transactionId }.first()}
        Assertions.assertThat(current).isNotNull
    }
    @Test
    fun `deleting account is not possible when transactions are available`() {

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
        Assertions.assertThatThrownBy { transaction { account1.delete() } }
            .isInstanceOf(ExposedSQLException::class.java)
    }
}