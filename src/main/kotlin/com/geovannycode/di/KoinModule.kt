package com.geovannycode.di

import com.geovannycode.repository.*
import io.ktor.server.application.Application
import io.ktor.server.application.install
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.SLF4JLogger

val bankingModule = module {
    single<UserRepository> { DefaultUserRepository() }
    single<AccountRepository> { DefaultAccountRepository() }
    single<TransactionRepository> { DefaultTransactionRepository() }
}

fun Application.setupKoin() {
    install(Koin) {
        SLF4JLogger()
        modules(bankingModule)
    }
}