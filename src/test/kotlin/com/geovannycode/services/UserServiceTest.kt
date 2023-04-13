package com.geovannycode.services

import com.geovannycode.TestDatabaseFactory
import com.geovannycode.di.bankingModule
import com.geovannycode.dto.ApiResult
import com.geovannycode.dto.ErrorCode
import com.geovannycode.dto.UserDto
import com.geovannycode.service.UserService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.koin.test.KoinTest
import org.koin.test.inject
import org.koin.test.junit5.KoinTestExtension
import java.util.*

internal class UserServiceTest : KoinTest {

    private lateinit var databaseFactory: TestDatabaseFactory
    private val userService by inject<UserService>()

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
    fun `create new user is possible`() {
        val user = UserDto(
            firstName = "Geovanny",
            lastName = "Mendoza",
            birthDate = "20.02.1999",
            password = "Ta1&tudol3lal54e"
        )

        val actual = userService.createUser(user)

        assertThat(actual).isInstanceOf(ApiResult.Success::class.java)
        assertThat((actual as ApiResult.Success).value).isNotNull
    }

    @Test
    fun `create new user fails if userId already exists in database`() {
        val user = UserDto(
            firstName = "Geovanny",
            lastName = "Mendoza",
            birthDate = "20.02.1999",
            password = "Ta1&tudol3lal54e"
        )
        userService.createUser(user)
        val actual = userService.createUser(user)
        assertThat(actual).isInstanceOf(ApiResult.Failure::class.java)
        assertThat((actual as ApiResult.Failure).errorCode).isEqualTo(ErrorCode.DATABASE_ERROR)
    }


    @Test
    fun `create new user fails if birthdate is not parsable`() {
        val user = UserDto(
            firstName = "Geovanny",
            lastName = "Mendoza",
            birthDate = "20-02-1999",
            password = "password"
        )
        val actual = userService.createUser(user)
        assertThat(actual).isInstanceOf(ApiResult.Failure::class.java)
        assertThat((actual as ApiResult.Failure).errorCode).isEqualTo(ErrorCode.MAPPING_ERROR)
    }

    @Test
    fun `create new user fails if birthdate does not fulfill requirement`() {
        val user = UserDto(
            firstName = "Geovanny",
            lastName = "Mendoza",
            birthDate = "20.02.2019",
            password = "password"
        )
        val actual = userService.createUser(user)
        assertThat(actual).isInstanceOf(ApiResult.Failure::class.java)
        assertThat((actual as ApiResult.Failure).errorCode).isEqualTo(ErrorCode.MAPPING_ERROR)
    }

    @Test
    fun `create new user fails if password is too short`() {
        val user = UserDto(
            firstName = "Geovanny",
            lastName = "Mendoza",
            birthDate = "20.02.2000",
            password = "password"
        )
        val actual = userService.createUser(user)
        assertThat(actual).isInstanceOf(ApiResult.Failure::class.java)
        assertThat((actual as ApiResult.Failure).errorCode).isEqualTo(ErrorCode.MAPPING_ERROR)
    }

    @Test
    fun `create new user fails if password does not contain a lowercase character`() {
        val user = UserDto(
            firstName = "Geovanny",
            lastName = "Mendoza",
            birthDate = "20.02.2000",
            password = "TTTTTTTTTTTTTTTT"
        )
        val actual = userService.createUser(user)
        assertThat(actual).isInstanceOf(ApiResult.Failure::class.java)
        assertThat((actual as ApiResult.Failure).errorCode).isEqualTo(ErrorCode.MAPPING_ERROR)
    }

    @Test
    fun `create new user fails if password does not contain a uppercase character`() {
        val user = UserDto(
            firstName = "Geovanny",
            lastName = "Mendoza",
            birthDate = "20.02.2000",
            password = "tttttttttttttttt"
        )
        val actual = userService.createUser(user)
        assertThat(actual).isInstanceOf(ApiResult.Failure::class.java)
        assertThat((actual as ApiResult.Failure).errorCode).isEqualTo(ErrorCode.MAPPING_ERROR)
    }

    @Test
    fun `create new user fails if password does not contain a special character`() {
        val user = UserDto(
            firstName = "Geovanny",
            lastName = "Mendoza",
            birthDate = "20.02.2000",
            password = "Atttttttttttttttt"
        )
        val actual = userService.createUser(user)
        assertThat(actual).isInstanceOf(ApiResult.Failure::class.java)
        assertThat((actual as ApiResult.Failure).errorCode).isEqualTo(ErrorCode.MAPPING_ERROR)
    }

    @Test
    fun `create new user fails if password does not contain a digit`() {
        val user = UserDto(
            firstName = "Geovanny",
            lastName = "Mendoza",
            birthDate = "20.02.2000",
            password = "Attttttttttttttt1"
        )
        val actual = userService.createUser(user)
        assertThat(actual).isInstanceOf(ApiResult.Failure::class.java)
        assertThat((actual as ApiResult.Failure).errorCode).isEqualTo(ErrorCode.MAPPING_ERROR)
    }

    @Test
    fun `delete existing user is possible`() {
        val user = UserDto(
            firstName = "Geovanny",
            lastName = "Mendoza",
            birthDate = "20.02.1999",
            password = "Ta1&tudol3lal54e"
        )
        val apiResult = userService.createUser(user)
        val userId = (apiResult as ApiResult.Success).value

        val actual = userService.deleteUser(userId.toString())

        assertThat(actual).isInstanceOf(ApiResult.Success::class.java)
        assertThat((actual as ApiResult.Success).value).isEqualTo(userId)
    }

    @Test
    fun `delete is not possible because user does not exist`() {
        val userId = UUID.randomUUID()
        val actual = userService.deleteUser(userId.toString())
        assertThat(actual).isInstanceOf(ApiResult.Failure::class.java)
        assertThat((actual as ApiResult.Failure).errorCode).isEqualTo(ErrorCode.USER_NOT_FOUND)
    }

    @Test
    fun `delete is not possible because userId is not valid`() {
        val userId = "invalid_userId"
        val actual = userService.deleteUser(userId)
        assertThat(actual).isInstanceOf(ApiResult.Failure::class.java)
        assertThat((actual as ApiResult.Failure).errorCode).isEqualTo(ErrorCode.MAPPING_ERROR)
    }
}