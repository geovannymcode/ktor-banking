package com.geovannycode

import com.geovannycode.entities.DatabaseFactory
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.h2.tools.RunScript
import org.jetbrains.exposed.sql.Database
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.random.Random

class TestDatabaseFactory : DatabaseFactory {

   private lateinit var source: HikariDataSource
    override fun connect() {
        source = hikari()
        SchemaDefinition.createSchema(source)
        Database.connect(hikari())
    }

    private fun hikari(): HikariDataSource{
        val config = HikariConfig()
        config.driverClassName = "org.h2.Driver"
        config.jdbcUrl = "jdbc:h2:mem:db${Random.nextLong(10000,99999)};"
        config.username="root"
        config.password="password"
        config.maximumPoolSize = 2
        config.isAutoCommit = true
        config.validate()
        return HikariDataSource(config)
    }

    fun close(){
        source.close()
    }

    object SchemaDefinition{
        fun createSchema(dataSource: HikariDataSource){
            RunScript.execute(
                dataSource.connection, Files.newBufferedReader(
                    Paths.get("src/main/resources/db/schema.sql")
                )
            )
        }
    }
}