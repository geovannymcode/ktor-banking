package com.geovannycode.dto

import java.util.UUID

data class UserOverviewDto(
    val userId: UUID? = null,
    val firstName: String,
    val lastName: String,
    val birthdate: String,
    val password: String,
    val created: String,
    val lastUpdated: String,
    val account: List<AccountOverviewDto>
)
