package com.geovannycode.repository

import com.geovannycode.entities.account.AccountEntity
import com.geovannycode.entities.account.AccountTable
import com.geovannycode.entities.user.UserEntity
import com.geovannycode.entities.user.UserTable
import com.geovannycode.models.Account
import com.geovannycode.models.User
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime

class DefaultAccountRepository : AccountRepository {
    override fun saveForUser(user: User, account: Account): Account = transaction {
        val existingUser = UserEntity.find { UserTable.userId eq user.userId }.firstOrNull()?: error("User with userId '${user.userId}' not persisted yet!")
        val currentDateTime = LocalDateTime.now()
        val existingAccount = AccountEntity.find { AccountTable.accountId eq account.accountId }.singleOrNull()
        if(existingAccount==null){
             AccountEntity.new {
                accountId = account.accountId
                name= account.name
                balance=account.balance
                dispo = account.dispo
                limit= account.limit
                created = currentDateTime
                lastUpdated =  currentDateTime
                userEntity= existingUser
            }
            account.copy(
                created = currentDateTime,
                lastUpdated = currentDateTime
            )
        }else{
            error("User with userId '${user.userId} already exists.")
        }
    }

    override fun delete(account: Account) = transaction{
       AccountEntity.find { AccountTable.accountId eq account.accountId }.firstOrNull().let {
           if(it==null){
               error("Account '${account.accountId}' does not exist!")
           }else{
               it.userEntity = null
           }
       }
    }

    override fun updateForUser(user: User, account: Account): Account = transaction {
        val existingUser = UserEntity.find { UserTable.userId eq user.userId }.firstOrNull()
            ?: error("User with userId '${user.userId}' not persisted yet!")
        val currentDateTime = LocalDateTime.now()
        val existingAccount = AccountEntity.find { AccountTable.accountId eq account.accountId }.firstOrNull()
            ?: error("Account with accountId '${account.accountId} not persisted yet! ")

        existingAccount.accountId = account.accountId
        existingAccount.name = account.name
        existingAccount.balance = account.balance
        existingAccount.dispo = account.dispo
        existingAccount.limit = account.limit
        existingAccount.lastUpdated = currentDateTime
        existingAccount.userEntity = existingUser
        account.copy(
            lastUpdated = currentDateTime
        )
    }
}

fun AccountEntity.toAccount() = Account(
    name = this.name,
    accountId = this.accountId,
    balance = this.balance,
    dispo = this.dispo,
    limit = this.limit,
    created = this.created,
    lastUpdated = this.lastUpdated
)