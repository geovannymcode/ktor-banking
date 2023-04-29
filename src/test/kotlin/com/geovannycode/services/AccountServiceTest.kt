package com.geovannycode.services

import com.geovannycode.TestDatabaseFactory
import com.geovannycode.di.bankingModule
import com.geovannycode.dto.AccountDto
import com.geovannycode.dto.ApiResult
import com.geovannycode.dto.ErrorCode
import com.geovannycode.models.Account
import com.geovannycode.models.User
import com.geovannycode.repository.AccountRepository
import com.geovannycode.repository.UserRepository
import com.geovannycode.service.AccountService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.koin.test.KoinTest
import org.koin.test.inject
import org.junit.jupiter.api.extension.RegisterExtension
import org.koin.test.junit5.KoinTestExtension
import org.junit.jupiter.api.Test
import java.time.LocalDate


internal class AccountServiceTest : KoinTest {
    private lateinit var databaseFactory: TestDatabaseFactory
    private val accountService by inject<AccountService>()
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
    fun `createAccount is possible`() {
        val user = User(
            firstName = "Geovanny",
            lastName = "Mendoza",
            birthdate = LocalDate.of(1999,1,1),
            password = "Ta1&tudol3lal54e"
        )
        val persistedUser = userRepository.save(user)

        val account = AccountDto(
            name = "My Account",
            dispo = -100.0,
            limit = 100.0
        )

        val actual = accountService.createAccount(userId = persistedUser.userId, accountDto = account)

        assertThat(actual).isInstanceOf(ApiResult.Success::class.java)
        assertThat((actual as ApiResult.Success).value).isNotNull
    }

    @Test
    fun `createAccount fails if the user is not available in database`() {
        val user = User(
            firstName = "Geovanny",
            lastName = "Mendoza",
            birthdate = LocalDate.of(1999,1,1),
            password = "Ta1&tudol3lal54e"
        )

        val account = AccountDto(
            name = "My Account",
            dispo = -100.0,
            limit = 100.0
        )

        val actual = accountService.createAccount(userId = user.userId, accountDto = account)

        assertThat(actual).isInstanceOf(ApiResult.Failure::class.java)
        assertThat((actual as ApiResult.Failure).errorCode).isEqualTo(ErrorCode.USER_NOT_FOUND)
    }

    @Test
    fun `createAccount fails if account limit is negative`() {
        val user = User(
            firstName = "Geovanny",
            lastName = "Mendoza",
            birthdate = LocalDate.of(1999,1,1),
            password = "Ta1&tudol3lal54e"
        )
        val persistedUser = userRepository.save(user)

        val account = AccountDto(
            name = "My Account",
            dispo = -100.0,
            limit = -100.0
        )

        val actual = accountService.createAccount(userId = persistedUser.userId, accountDto = account)

        assertThat(actual).isInstanceOf(ApiResult.Failure::class.java)
        assertThat((actual as ApiResult.Failure).errorCode).isEqualTo(ErrorCode.MAPPING_ERROR)
    }

    @Test
    fun `createAccount fails if account is already available by accountId`() {
        val user = User(
            firstName = "Geovanny",
            lastName = "Mendoza",
            birthdate = LocalDate.of(1999,1,1),
            password = "Ta1&tudol3lal54e"
        )
        val persistedUser = userRepository.save(user)

        val account = Account(
            name = "My Account",
            dispo = -100.0,
            limit = 100.0
        )
        val persistedAccount = accountRepository.saveForUser(user = persistedUser, account = account)

        val actual = accountService.createAccount(
            userId = persistedUser.userId, accountDto = AccountDto(
                accountId = persistedAccount.accountId,
                name = "My Other Account",
                dispo = persistedAccount.dispo,
                limit = persistedAccount.limit
            )
        )

        assertThat(actual).isInstanceOf(ApiResult.Failure::class.java)
        assertThat((actual as ApiResult.Failure).errorCode).isEqualTo(ErrorCode.ACCOUNT_ALREADY_EXIST)
    }

    @Test
    fun `createAccount fails if account is already available by name`() {
        val user = User(
            firstName = "Geovanny",
            lastName = "Mendoza",
            birthdate = LocalDate.of(1999,1,1),
            password = "Ta1&tudol3lal54e"
        )
        val persistedUser = userRepository.save(user)

        val account = Account(
            name = "My Account",
            dispo = -100.0,
            limit = 100.0
        )
        val persistedAccount = accountRepository.saveForUser(user = persistedUser, account = account)

        val actual = accountService.createAccount(
            userId = persistedUser.userId, accountDto = AccountDto(
                name = persistedAccount.name,
                dispo = persistedAccount.dispo,
                limit = persistedAccount.limit
            )
        )

        assertThat(actual).isInstanceOf(ApiResult.Failure::class.java)
        assertThat((actual as ApiResult.Failure).errorCode).isEqualTo(ErrorCode.DATABASE_ERROR)
    }
}