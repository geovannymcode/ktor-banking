package com.geovannycode.models

import java.time.LocalDateTime
import java.util.UUID

data class Account(
    val name: String,
    val accountId: UUID = UUID.randomUUID(),
    val balance: Double,
    val dispo: Double,
    val limit: Double,
    val created: LocalDateTime = LocalDateTime.now(),
    val lastUpdated: LocalDateTime = LocalDateTime.now(),
)
