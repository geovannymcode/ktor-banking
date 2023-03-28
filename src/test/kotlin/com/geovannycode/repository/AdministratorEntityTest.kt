package com.geovannycode.repository

import com.geovannycode.TestDatabaseFactory
import com.geovannycode.entities.administrator.AdministratorEntity
import com.geovannycode.entities.administrator.AdministratorTable
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

internal class AdministratorEntityTest {

    private lateinit var databaseFactory: TestDatabaseFactory

    @BeforeEach
    fun setupDatasource() {
        databaseFactory = TestDatabaseFactory()
        databaseFactory.connect()
    }

    @AfterEach
    fun tearDownDatasource() {
        databaseFactory.close()
    }

    @Test
    fun `creating new administrator is possible`() {
        val administrator = transaction {
            AdministratorEntity.new {
                adminId = UUID.randomUUID()
                name = "Admin"
                password = "test"
            }
        }
        assertThat(transaction { AdministratorEntity.findById(administrator.id) }).isNotNull
    }

    @Test
    fun `deleting an administrator is possible`() {
        val persistedAdministrator = transaction {
            AdministratorEntity.new {
                adminId = UUID.randomUUID()
                name = "Admin"
                password = "test"
            }
        }
        transaction { persistedAdministrator.delete() }
        assertThat(transaction { AdministratorEntity.findById(persistedAdministrator.id) }).isNull()
    }

    @Test
    fun `editing an administrator is possible`() {
        val persistedAdministrator = transaction {
            AdministratorEntity.new {
                adminId = UUID.randomUUID()
                name = "Admin"
                password = "test"
            }
        }
        transaction { persistedAdministrator.name = "Manager" }
        assertThat(transaction { AdministratorEntity.findById(persistedAdministrator.id)!!.name }).isEqualTo("Manager")
    }

    @Test
    fun `finding an administrator is possible`() {
        transaction {
            AdministratorEntity.new {
                adminId = UUID.randomUUID()
                name = "Admin"
                password = "test"
            }
        }
        val actual = transaction { AdministratorEntity.find { AdministratorTable.name eq "Admin" }.firstOrNull() }
        assertThat(actual).isNotNull
    }
}