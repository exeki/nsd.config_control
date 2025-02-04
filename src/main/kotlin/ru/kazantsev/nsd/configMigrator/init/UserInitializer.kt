package ru.kazantsev.nsd.configMigrator.init

import org.slf4j.LoggerFactory
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import ru.kazantsev.nsd.configMigrator.data.model.User
import ru.kazantsev.nsd.configMigrator.data.model.UserAuthority
import ru.kazantsev.nsd.configMigrator.data.repo.UserAuthorityRepo
import ru.kazantsev.nsd.configMigrator.data.repo.UserRepo

@Component
class UserInitializer(
    private val userRepository: UserRepo,
    private val userAuthorityRepo: UserAuthorityRepo,
    private val userRepo: UserRepo,
    private val passwordEncoder: PasswordEncoder
) : ApplicationRunner {

    fun getAuthoritiesForCreate(): MutableList<UserAuthority> {
        return mutableListOf(
            UserAuthority().apply {
                this.code = "admin"
                this.title = "Администратор"
            },
            UserAuthority().apply {
                this.code = "user"
                this.title = "Пользователь"
            }
        )
    }

    fun getUsersForCreate(): MutableList<User> {
        return mutableListOf(
            User().apply {
                username = "admin"
                password = passwordEncoder.encode("admin")
                middleName = "Админович"
                lastName = "Админов"
                firstName = "Админ"
                authorities = mutableSetOf(userAuthorityRepo.findByCode("admin").get())
            }
        )
    }

    val log = LoggerFactory.getLogger(UserInitializer::class.java)!!

    fun createAuthorities() {
        getAuthoritiesForCreate().forEach {
            if (userAuthorityRepo.findByCode(it.code).isEmpty) userAuthorityRepo.save(it)
        }
    }

    fun createUsers() {
        getUsersForCreate().forEach {
            if (userRepo.findByUsername(it.username).isEmpty) userRepo.save(it)
        }
    }

    @Throws(Exception::class)
    override fun run(args: ApplicationArguments) {
        createAuthorities()
        createUsers()
    }
}
