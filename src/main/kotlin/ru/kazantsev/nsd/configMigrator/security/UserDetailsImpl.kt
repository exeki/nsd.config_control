package ru.kazantsev.nsd.configMigrator.security

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import ru.kazantsev.nsd.configMigrator.data.model.User
import ru.kazantsev.nsd.configMigrator.data.model.UserAuthority

class UserDetailsImpl(val user: User) : UserDetails {

    override fun isAccountNonExpired(): Boolean = user.isAccountNonExpired

    fun isAccountNonExpired(value: Boolean) = value.also { user.isAccountNonExpired = it }

    override fun isAccountNonLocked(): Boolean = user.isAccountNonLocked

    fun isAccountNonLocked(value: Boolean) = value.also { user.isAccountNonLocked = it }

    override fun isCredentialsNonExpired(): Boolean = user.isCredentialsNonExpired

    fun isCredentialsNonExpired(value: Boolean) = value.also { user.isCredentialsNonExpired = value }

    override fun isEnabled(): Boolean = user.isEnabled

    fun isEnabled(value: Boolean) = value.also { user.isEnabled = it }

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> = user.authorities

    fun setAuthorities(value: MutableSet<UserAuthority>) = value.also { user.authorities = it }

    override fun getPassword(): String = user.password

    fun setPassword(value: String) = value.also { user.password = value }

    override fun getUsername(): String = user.username

    fun setUsername(value: String) = value.also { user.username = value }
}