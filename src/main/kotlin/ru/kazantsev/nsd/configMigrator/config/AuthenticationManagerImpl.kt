package ru.kazantsev.nsd.configMigrator.config

import org.slf4j.LoggerFactory
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import ru.kazantsev.nsd.configMigrator.data.repo.UserRepo

@Component
class AuthenticationManagerImpl(
    private val passwordEncoder: PasswordEncoder,
    private val userRepo: UserRepo
) : AuthenticationManager {

    @Throws(AuthenticationException::class)
    override fun authenticate(auth: Authentication): Authentication {
        val username: String = auth.name
        val password: String = auth.credentials.toString()
        val user = userRepo.findByUsername(username).orElse(null)
        if (user != null && passwordEncoder.matches(auth.credentials as String, user.password)) {
            return UsernamePasswordAuthenticationToken(user, password, user.authorities)
        } else throw BadCredentialsException("Authentication failed")
    }
}