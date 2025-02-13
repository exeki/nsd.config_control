package ru.kazantsev.nsd.configMigrator.data.model

import jakarta.persistence.Entity
import jakarta.persistence.ManyToOne

@Entity
class AccessKey() : AbstractEntity() {
    lateinit var accessKey: String
    @ManyToOne
    lateinit var user : User
    @ManyToOne
    lateinit var installation: Installation

    constructor(accessKey: String, user: User, installation: Installation) : this() {
        this.accessKey = accessKey
        this.user = user
        this.installation = installation
    }
}