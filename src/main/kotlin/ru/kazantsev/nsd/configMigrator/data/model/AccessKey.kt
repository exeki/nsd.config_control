package ru.kazantsev.nsd.configMigrator.data.model

import jakarta.persistence.Entity
import jakarta.persistence.ManyToOne
import java.time.LocalDateTime

@Entity
class AccessKey() : AbstractEntity() {
    lateinit var accessKey: String
    @ManyToOne
    lateinit var user : User
    @ManyToOne
    lateinit var installation: Installation
    lateinit var date : LocalDateTime
    var expired = false

    constructor(accessKey: String, user: User, installation: Installation, date : LocalDateTime) : this() {
        this.accessKey = accessKey
        this.user = user
        this.installation = installation
        this.date = date
    }
}