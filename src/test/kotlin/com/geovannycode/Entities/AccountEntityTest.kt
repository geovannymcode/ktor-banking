package com.geovannycode.Entities

import com.geovannycode.TestDatabaseFactory
import com.geovannycode.entities.account.AccountEntity
import com.geovannycode.entities.account.AccountTable
import com.geovannycode.entities.transaction.TransactionEntity
import com.geovannycode.entities.user.UserEntity
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
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
        assertThat(current).isNotNull
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
        assertThat(current).isNotNull
    }

    @Test
    fun `updating account set user to null possible`() {
        val user = transaction {
            UserEntity.new {
                userId = UUID.randomUUID()
                firstName = "Geovanny"
                lastName = "Mendoza"
                birthdate = LocalDate.of(2000,1,1)
                password = "test"
                created = LocalDateTime.of(2023, 1, 1, 1, 9)
                lastUpdated = LocalDateTime.of(2023, 1, 1, 2, 9)
            }
        }

            val account = transaction {
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
        transaction { account.userEntity = null }
        assertThat(transaction { AccountEntity.findById(account.id)!!.userEntity }).isNull()
    }

    @Test
    fun `delete user also set relation to account to null`() {

        val user = transaction {
            UserEntity.new {
                userId = UUID.randomUUID()
                firstName = "Geovanny"
                lastName = "Mendoza"
                birthdate = LocalDate.of(2000,1,1)
                password = "test"
                created = LocalDateTime.of(2023, 1, 1, 1, 9)
                lastUpdated = LocalDateTime.of(2023, 1, 1, 2, 9)
            }
        }

        val persistedAccount = transaction {
            AccountEntity.new {
                name = "My Account"
                accountId = UUID.randomUUID()
                balance = 120.0
                dispo = -100.0
                limit = 100.0
                created = LocalDateTime.of(2023, 1, 2, 1, 9)
                lastUpdated = LocalDateTime.of(2023, 1, 2, 2, 9)
                userEntity = user
            }
        }

        transaction { user.delete() }

        assertThat(transaction { AccountEntity.findById(persistedAccount.id) }).isNotNull
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
                created = LocalDateTime.of(2023, 1, 1, 1, 9)
                lastUpdated = LocalDateTime.of(2023, 1, 1, 2, 9)
            }
        }

        val persistedAccount = transaction {
            AccountEntity.new {
                name = "My Account"
                accountId = UUID.randomUUID()
                balance = 120.0
                dispo = -100.0
                limit = 100.0
                created = LocalDateTime.of(2022, 1, 2, 1, 9)
                lastUpdated = LocalDateTime.of(2022, 1, 2, 2, 9)
                userEntity = user
            }
        }

        val actual = transaction { AccountEntity.find { AccountTable.accountId eq persistedAccount.accountId } }

        assertThat(actual).isNotNull
    }

    @Test
    fun `creating new account is not possible with duplicate accountId`() {
        val user = transaction {
            UserEntity.new {
                userId = UUID.randomUUID()
                firstName = "Geovanny"
                lastName = "Mendoza"
                birthdate = LocalDate.of(2002, 1, 1)
                password = "passw0rd"
                created = LocalDateTime.of(2022, 1, 1, 1, 9)
                lastUpdated = LocalDateTime.of(2022, 1, 1, 2, 9)
            }
        }

        val persistedAccount = transaction {
            AccountEntity.new {
                name = "My Account"
                accountId = UUID.randomUUID()
                balance = 120.0
                dispo = -100.0
                limit = 100.0
                created = LocalDateTime.of(2022, 1, 2, 1, 9)
                lastUpdated = LocalDateTime.of(2022, 1, 2, 2, 9)
                userEntity = user
            }
        }

        assertThatThrownBy {
            transaction {
                AccountEntity.new {
                    name = "My Account"
                    accountId = persistedAccount.accountId
                    balance = 120.0
                    dispo = -100.0
                    limit = 100.0
                    created = LocalDateTime.of(2022, 1, 2, 1, 9)
                    lastUpdated = LocalDateTime.of(2022, 1, 2, 2, 9)
                    userEntity = user
                }
            }
        }
    }
    @Test
    fun `creating new account is not possible with duplicate name`() {
        // given
        val user = transaction {
            UserEntity.new {
                userId = UUID.randomUUID()
                firstName = "John"
                lastName = "Doe"
                birthdate = LocalDate.of(2000, 1, 1)
                password = "passw0rd"
                created = LocalDateTime.of(2022, 1, 1, 1, 9)
                lastUpdated = LocalDateTime.of(2022, 1, 1, 2, 9)
            }
        }

        transaction {
            AccountEntity.new {
                name = "My Account"
                accountId = UUID.randomUUID()
                balance = 120.0
                dispo = -100.0
                limit = 100.0
                created = LocalDateTime.of(2022, 1, 2, 1, 9)
                lastUpdated = LocalDateTime.of(2022, 1, 2, 2, 9)
                userEntity = user
            }
        }

        // when + then
        assertThatThrownBy {
            transaction {
                AccountEntity.new {
                    name = "My Account"
                    accountId = UUID.randomUUID()
                    balance = 120.0
                    dispo = -100.0
                    limit = 100.0
                    created = LocalDateTime.of(2022, 1, 2, 1, 9)
                    lastUpdated = LocalDateTime.of(2022, 1, 2, 2, 9)
                    userEntity = user
                }
            }
        }
    }

    @Test
    fun `creating new account is possible with duplicate name for different user`() {
        val user = transaction {
            UserEntity.new {
                userId = UUID.randomUUID()
                firstName = "Geovanny"
                lastName = "Mendoza"
                birthdate = LocalDate.of(2002, 1, 1)
                password = "passw0rd"
                created = LocalDateTime.of(2022, 1, 1, 1, 9)
                lastUpdated = LocalDateTime.of(2022, 1, 1, 2, 9)
            }
        }

        val otherUser = transaction {
            UserEntity.new {
                userId = UUID.randomUUID()
                firstName = "Manuel"
                lastName = "Mendoza"
                birthdate = LocalDate.of(2002, 1, 1)
                password = "passw0rd"
                created = LocalDateTime.of(2022, 1, 1, 1, 9)
                lastUpdated = LocalDateTime.of(2022, 1, 1, 2, 9)
            }
        }

        transaction {
            AccountEntity.new {
                name = "My Account"
                accountId = UUID.randomUUID()
                balance = 120.0
                dispo = -100.0
                limit = 100.0
                created = LocalDateTime.of(2022, 1, 2, 1, 9)
                lastUpdated = LocalDateTime.of(2022, 1, 2, 2, 9)
                userEntity = user
            }
        }

        val persistedAccount = transaction {
            AccountEntity.new {
                name = "My Account"
                accountId = UUID.randomUUID()
                balance = 120.0
                dispo = -100.0
                limit = 100.0
                created = LocalDateTime.of(2022, 1, 2, 1, 9)
                lastUpdated = LocalDateTime.of(2022, 1, 2, 2, 9)
                userEntity = otherUser
            }
        }

        assertThat(transaction { AccountEntity.findById(persistedAccount.id) }).isNotNull
    }
}