package com.geovannycode.service

import com.geovannycode.dto.AccountDto
import com.geovannycode.dto.ApiResult
import java.util.UUID

interface AccountService {
    fun createAccount(userId: UUID, accountDto: AccountDto): ApiResult<UUID>
}