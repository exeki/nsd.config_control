package ru.kazantsev.nsd.configMigrator.data.model

import jakarta.persistence.Entity
import org.springframework.security.core.GrantedAuthority

@Entity
class UserAuthority() : AbstractEntity(), GrantedAuthority {
    lateinit var code: String
    lateinit var title: String
    override fun getAuthority(): String = code
}