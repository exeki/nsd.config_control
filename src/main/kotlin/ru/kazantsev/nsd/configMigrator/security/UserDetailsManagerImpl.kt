package ru.kazantsev.nsd.configMigrator.security

import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.provisioning.UserDetailsManager
import org.springframework.stereotype.Component
import ru.kazantsev.nsd.configMigrator.data.model.User
import ru.kazantsev.nsd.configMigrator.data.repo.UserAuthorityRepo
import ru.kazantsev.nsd.configMigrator.data.repo.UserRepo
import ru.kazantsev.nsd.configMigrator.exception.AuthorityNotFoundException

@Component
class UserDetailsManagerImpl(
    private val userRepo: UserRepo,
    private val userAuthorityRepo: UserAuthorityRepo
) : UserDetailsManager {

    override fun loadUserByUsername(username: String): UserDetails {
        return loadUserDataByUsername(username)
    }

    private fun loadUserDataByUsername(username: String): User {
        return userRepo.findByUsername(username).orElseThrow { UsernameNotFoundException("$username not found") }
    }

    override fun createUser(userDetails: UserDetails) {
        val user = User().apply {
            username = userDetails.username
            password = userDetails.password
            isEnabled = userDetails.isEnabled
            isAccountNonLocked = userDetails.isAccountNonLocked
            isCredentialsNonExpired = userDetails.isCredentialsNonExpired
            isAccountNonExpired = userDetails.isAccountNonExpired
            authorities = userDetails.authorities.map {
                userAuthorityRepo.findByCode(it.authority).orElseThrow {
                    AuthorityNotFoundException("Authority ${it.authority} not found")
                }
            }.toMutableSet()
        }
        userRepo.save(user)
    }

    override fun updateUser(userDetails: UserDetails) {
        val user = loadUserDataByUsername(userDetails.username)
        user.apply {
            username = userDetails.username
            password = userDetails.password
            isEnabled = userDetails.isEnabled
            isAccountNonLocked = userDetails.isAccountNonLocked
            isCredentialsNonExpired = userDetails.isCredentialsNonExpired
            isAccountNonExpired = userDetails.isAccountNonExpired
            authorities = userDetails.authorities.map {
                userAuthorityRepo.findByCode(it.authority).orElseThrow {
                    AuthorityNotFoundException("Authority ${it.authority} not found")
                }
            }.toMutableSet()
        }
        userRepo.save(user)
    }

    override fun deleteUser(username: String) {
        val user = loadUserDataByUsername(username)
        userRepo.delete(user)
    }

    override fun changePassword(oldPassword: String?, newPassword: String?) {
        throw RuntimeException("Как вообще нахуй имплементировать этот гениальный метод changePassword")
    }

    override fun userExists(username: String): Boolean {
        return userRepo.findByUsername(username).isPresent
    }

}
