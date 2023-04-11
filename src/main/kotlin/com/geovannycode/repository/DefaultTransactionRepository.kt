package com.geovannycode.repository

import com.geovannycode.entities.account.AccountEntity
import com.geovannycode.entities.account.AccountTable
import com.geovannycode.entities.transaction.TransactionEntity
import com.geovannycode.entities.transaction.TransactionTable
import com.geovannycode.models.Account
import com.geovannycode.models.Transaction
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.or
import java.time.LocalDateTime

class DefaultTransactionRepository: TransactionRepository {
    override fun save(transaction: Transaction): Transaction = transaction {
        val currentDateTime = LocalDateTime.now()
        val existingTransaction = TransactionEntity.find { TransactionTable.transactionId eq transaction.transactionId }.firstOrNull()

        if (existingTransaction == null) {
            TransactionEntity.new {
                transactionId = transaction.transactionId
                originEntity = AccountEntity.find { AccountTable.accountId eq transaction.origin.accountId }.firstOrNull()
                        ?: error("Account '${transaction.origin.accountId}' not available in database!")
                targetEntity = AccountEntity.find { AccountTable.accountId eq transaction.target.accountId }.firstOrNull()
                        ?: error("Account '${transaction.target.accountId}' not available in database!")
                amount = transaction.amount
                created = LocalDateTime.now()
            }
            transaction.copy(
                created = currentDateTime
            )
        }else{
            error("Transactions cannot be updated!")
        }

    }

    override fun findAllByAccount(account: Account): List<Transaction> = transaction {
           TransactionEntity.find { TransactionTable.origin eq account.accountId or (TransactionTable.target eq account.accountId)}
               .map { it.toTransaction() }
       }


}

fun TransactionEntity.toTransaction() = Transaction(
    transactionId = this.transactionId,
    origin = this.originEntity.toAccount(),
    target = this.targetEntity.toAccount(),
    amount = this.amount,
    created = this.created
)