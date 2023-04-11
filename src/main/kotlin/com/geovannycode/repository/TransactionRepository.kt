package com.geovannycode.repository

import com.geovannycode.models.Account
import com.geovannycode.models.Transaction

interface TransactionRepository {
    fun save(transaction: Transaction): Transaction

    fun findAllByAccount(account: Account): List<Transaction>
}