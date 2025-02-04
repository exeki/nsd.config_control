package ru.kazantsev.nsd.configMigrator.data.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.ManyToMany
import jakarta.persistence.Table
import jakarta.validation.constraints.NotBlank
import org.springframework.security.core.userdetails.UserDetails

@Entity
@Table(name = "users")
class User() : AbstractEntity(), UserDetails {

    constructor(username: String, password: String) : this() {
        this.username = username
        this.password = password
    }

    @NotBlank
    @Column(unique = true, nullable = false)
    @JvmField
    final var username: String = ""

    @NotBlank
    @Column(nullable = false, length = 1000)
    @JvmField
    final var password: String= ""

    val fullName : String
        get() = "$lastName $firstName $middleName".trim()

    @NotBlank
    var lastName : String = ""

    var firstName : String = ""

    var middleName : String = ""

    @JvmField
    final var isAccountNonExpired: Boolean = true

    @JvmField
    final var isAccountNonLocked: Boolean = true

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

    override fun isEnabled(): Boolean = archived

    override fun isAccountNonExpired(): Boolean = true

    override fun isAccountNonLocked(): Boolean = archived

    override fun isCredentialsNonExpired(): Boolean = true

}