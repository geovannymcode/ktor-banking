package com.geovannycode.di

import com.geovannycode.repository.UserRepository
import com.geovannycode.repository.AccountRepository
import com.geovannycode.repository.TransactionRepository
import com.geovannycode.repository.DefaultTransactionRepository
import com.geovannycode.repository.DefaultUserRepository
import com.geovannycode.repository.DefaultAccountRepository
import com.geovannycode.service.DefaultUserService
import com.geovannycode.service.UserService
import io.ktor.server.application.Application
import io.ktor.server.application.install
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.SLF4JLogger

val bankingModule = module {
    single<UserRepository> { DefaultUserRepository() }
    single<AccountRepository> { DefaultAccountRepository() }
    single<TransactionRepository> { DefaultTransactionRepository() }
    single<UserService> { DefaultUserService(get()) }
}

fun Application.setupKoin() {
    install(Koin) {
        SLF4JLogger()
        modules(bankingModule)
    }
}