package com.geovannycode

import com.geovannycode.entities.DatabaseFactory
import com.geovannycode.entities.account.AccountTable
import com.geovannycode.entities.administrator.AdministratorTable
import com.geovannycode.entities.transaction.TransactionTable
import com.geovannycode.entities.user.UserTable
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

class TestDatabaseFactory : DatabaseFactory {

    private lateinit var source: HikariDataSource
    override fun connect() {
        Database.connect(hikari())
        SchemaDefinition.createSchema()
    }

    private fun hikari(): HikariDataSource{
        val config = HikariConfig()
        config.driverClassName = "org.h2.Driver"
        config.jdbcUrl = "jdbc:h2:mem:db"
        config.maximumPoolSize = 10
        config.isAutoCommit = true
        config.validate()
        source = HikariDataSource(config)
        return source
    }

    fun close(){
        source.close()
    }

    object SchemaDefinition{
        fun createSchema(){
            transaction {
                SchemaUtils.create(UserTable, AccountTable, TransactionTable, AdministratorTable)
            }
        }
    }
}