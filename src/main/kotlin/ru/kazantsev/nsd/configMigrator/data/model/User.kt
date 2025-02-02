package ru.kazantsev.nsd.configMigrator.data.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.ManyToMany
import jakarta.persistence.Table
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

@Entity
@Table(name = "app_user")
class User() : AbstractEntity(), UserDetails {

    constructor(username: String, password: String) : this() {
        this.username = username
        this.password = password
    }

    @Column(unique = true, nullable = false)
    @JvmField
    final var username: String = ""

    @Column(nullable = false)
    @JvmField
    final var password: String= ""

    val fullName : String
        get() = "$lastName $firstName $middleName".trim()

    var lastName : String = ""

    var firstName : String = ""

    var middleName : String = ""

    @JvmField
    final var isAccountNonExpired: Boolean = false

    @JvmField
    final var isAccountNonLocked: Boolean = false

    @JvmField
    final var isCredentialsNonExpired: Boolean = false

    @JvmField
    final var isEnabled: Boolean = false

    @JvmField
    @ManyToMany
    final var authorities: MutableSet<UserAuthority> = mutableSetOf()

    override fun getAuthorities(): MutableSet<UserAuthority> {
        return authorities
    }

    override fun getPassword(): String {
        return password
    }

    override fun getUsername(): String {
        return username
    }

}