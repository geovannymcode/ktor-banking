package com.geovannycode.repository

import com.geovannycode.models.Account
import com.geovannycode.models.User

interface AccountRepository {

    fun saveForUser(user: User, account: Account): Account

    fun delete(account: Account)
}

