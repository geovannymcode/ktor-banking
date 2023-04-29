package com.geovannycode.services

import com.geovannycode.TestDatabaseFactory
import com.geovannycode.di.bankingModule
import com.geovannycode.dto.ApiResult
import com.geovannycode.dto.ErrorCode
import com.geovannycode.dto.UserDto
import com.geovannycode.models.User
import com.geovannycode.repository.UserRepository
import com.geovannycode.service.UserService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.koin.test.KoinTest
import org.koin.test.inject
import org.koin.test.junit5.KoinTestExtension
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID

internal class UserServiceTest : KoinTest {

    private lateinit var databaseFactory: TestDatabaseFactory
    private val userService by inject<UserService>()
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
        val user = User(
            firstName = "Geovanny",
            lastName = "Mendoza",
            birthdate = LocalDate.of(1999,1,1),
            password = "Ta1&tudol3lal54e"
        )
        val persistedUser = userRepository.save(user)
        val actual = userService.deleteUser(persistedUser.userId.toString())

        assertThat(actual).isInstanceOf(ApiResult.Success::class.java)
        assertThat((actual as ApiResult.Success).value).isEqualTo(persistedUser.userId)
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

    @Test
    fun `update existing user is possible`() {
        val user = User(
            firstName = "Geovanny",
            lastName = "Mendoza",
            birthdate = LocalDate.of(1999,1,1),
            password = "Ta1&tudol3lal54e"
        )
        val persistedUser = userRepository.save(user)
        val actual = userService.updateUser(
            UserDto(
                userId = persistedUser.userId,
                firstName = "Manuel",
                lastName = "Gonzalez",
                birthDate = user.birthdate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                password = user.password
            )
        )
        assertThat(actual).isInstanceOf(ApiResult.Success::class.java)
        assertThat((actual as ApiResult.Success).value).isEqualTo(persistedUser.userId)
        assertThat(userRepository.findByUserId(persistedUser.userId)!!.firstName).isEqualTo("Manuel")
        assertThat(userRepository.findByUserId(persistedUser.userId)!!.lastName).isEqualTo("Gonzalez")
    }

    @Test
    fun `update user fails if password does not fulfill requirement`() {
        val user = User(
            firstName = "Geovanny",
            lastName = "Mendoza",
            birthdate = LocalDate.of(1999,1,1),
            password = "Ta1&tudol3lal54e"
        )
        val persistedUser = userRepository.save(user)
        val actual = userService.updateUser(
            UserDto(
                userId =  persistedUser.userId,
                firstName = "Manuel",
                lastName = "Mendoza",
                birthDate = user.birthdate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                password = "NOT VALID"
            )
        )
        assertThat(actual).isInstanceOf(ApiResult.Failure::class.java)
        assertThat((actual as ApiResult.Failure).errorCode).isEqualTo(ErrorCode.MAPPING_ERROR)
        assertThat(userRepository.findByUserId(persistedUser.userId)!!.firstName).isEqualTo("Geovanny")
        assertThat(userRepository.findByUserId(persistedUser.userId)!!.lastName).isEqualTo("Mendoza")
    }

    @Test
    fun  `findUserBy is possible`(){
        val user = User(
            firstName = "Geovanny",
            lastName = "Mendoza",
            birthdate = LocalDate.of(1999,1,1),
            password = "Ta1&tudol3lal54e"
        )
        val persistedUser = userRepository.save(user)
        val actual = userService.findUserByUserId(persistedUser.userId)

        assertThat(actual).isInstanceOf(ApiResult.Success::class.java)
        (actual as ApiResult.Success).value.apply {
            assertThat(this.userId).isEqualByComparingTo(persistedUser.userId)
            assertThat(this.firstName).isEqualTo("Geovanny")
            assertThat(this.lastName).isEqualTo("Mendoza")
            assertThat(this.birthdate).isEqualTo("01.01.1999")
            assertThat(this.password).isEqualTo("Ta1&tudol3lal54e")
            assertThat(this.created).isNotBlank
            assertThat(this.lastUpdated).isNotBlank
            assertThat(this.account).isEmpty()
        }
    }

    @Test
    fun  `findUserBy fails if user not available in database`(){
        val user = User(
            firstName = "Geovanny",
            lastName = "Mendoza",
            birthdate = LocalDate.of(1999,1,1),
            password = "Ta1&tudol3lal54e"
        )
        userRepository.save(user)
        val actual = userService.findUserByUserId(UUID.randomUUID())
        assertThat(actual).isInstanceOf(ApiResult.Failure::class.java)
        assertThat((actual as ApiResult.Failure).errorCode).isEqualTo(ErrorCode.USER_NOT_FOUND)
    }

    @Test
    fun `updatePassword is possible for existing user`() {
        val user = User(
            firstName = "Geovanny",
            lastName = "Mendoza",
            birthdate = LocalDate.of(1999,1,1),
            password = "Ta1&tudol3lal54e"
        )
        val persistedUser = userRepository.save(user)
        val actual = userService.updatePassword(persistedUser.userId, persistedUser.password, "Ta1&zuxcv3lal54e")
        assertThat(actual).isInstanceOf(ApiResult.Success::class.java)
        assertThat((actual as ApiResult.Success).value).isNotNull
    }

    @Test
    fun `updatePassword fails for none existing user`() {
        val user = User(
            firstName = "Geovanny",
            lastName = "Mendoza",
            birthdate = LocalDate.of(1999,1,1),
            password = "Ta1&tudol3lal54e"
        )
        val persistedUser = userRepository.save(user)
        val actual = userService.updatePassword(UUID.randomUUID(), persistedUser.password, "Ta1&zuxcv3lal54e")
        assertThat(actual).isInstanceOf(ApiResult.Failure::class.java)
        assertThat((actual as ApiResult.Failure).errorCode).isEqualTo(ErrorCode.USER_NOT_FOUND)
    }

    @Test
    fun `updatePassword fails if existing password and new password are equal`() {
        val user = User(
            firstName = "Geovanny",
            lastName = "Mendoza",
            birthdate = LocalDate.of(1999,1,1),
            password = "Ta1&tudol3lal54e"
        )
        val persistedUser = userRepository.save(user)
        val actual = userService.updatePassword(persistedUser.userId, persistedUser.password, "Ta1&tudol3lal54e")
        assertThat(actual).isInstanceOf(ApiResult.Failure::class.java)
        assertThat((actual as ApiResult.Failure).errorCode).isEqualTo(ErrorCode.PASSWORD_ERROR)
    }

    @Test
    fun `updatePassword fails if new password does not fulfill the requirements`() {
        val user = User(
            firstName = "Geovanny",
            lastName = "Mendoza",
            birthdate = LocalDate.of(1999,1,1),
            password = "Ta1&tudol3lal54e"
        )
        val persistedUser = userRepository.save(user)
        val actual = userService.updatePassword(persistedUser.userId, persistedUser.password, "NOT VALID")
        assertThat(actual).isInstanceOf(ApiResult.Failure::class.java)
        assertThat((actual as ApiResult.Failure).errorCode).isEqualTo(ErrorCode.PASSWORD_ERROR)
    }

    @Test
    fun `update user fails if user does not exist in database`() {
        val user = User(
            firstName = "Geovanny",
            lastName = "Mendoza",
            birthdate = LocalDate.of(1999,1,1),
            password = "Ta1&tudol3lal54e"
        )

        val actual = userService.updateUser(
            UserDto(
                userId = user.userId,
                firstName = "Manuel",
                lastName = "Gonzalez",
                birthDate = user.birthdate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                password = user.password
            )
        )
        assertThat(actual).isInstanceOf(ApiResult.Failure::class.java)
        assertThat((actual as ApiResult.Failure).errorCode).isEqualTo(ErrorCode.USER_NOT_FOUND)
    }
}