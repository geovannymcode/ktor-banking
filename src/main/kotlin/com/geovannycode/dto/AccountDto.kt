package com.geovannycode.dto

import java.util.UUID

data class AccountDto(
    val name: String,
    val accountId: UUID? = null,
    val dispo: Double,
    val limit: Double
)
