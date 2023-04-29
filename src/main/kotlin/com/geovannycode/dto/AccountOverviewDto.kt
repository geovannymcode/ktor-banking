package com.geovannycode.dto

import java.util.UUID

data class AccountOverviewDto (
    val name: String,
    val accountId: UUID,
    val balance: Double,
    val dispo: Double,
    val limit: Double,
    val created: String,
    val lastUpdated: String,
)