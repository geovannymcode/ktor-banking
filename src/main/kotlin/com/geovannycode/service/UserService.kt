package com.geovannycode.service

import com.geovannycode.dto.ApiResult
import com.geovannycode.dto.UserDto
import com.geovannycode.dto.UserOverviewDto
import java.util.UUID

interface UserService {
    fun createUser(userDto: UserDto): ApiResult<UUID>
    fun deleteUser(userId: String?): ApiResult<UUID>
    fun updateUser(userDto: UserDto): ApiResult<UUID>
    fun findUserByUserId(userId: UUID): ApiResult<UserOverviewDto>
}