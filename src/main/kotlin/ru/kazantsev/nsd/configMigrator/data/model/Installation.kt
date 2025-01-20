package ru.kazantsev.nsd.configMigrator.data.model

import java.time.LocalDateTime
import jakarta.persistence.Entity
import jakarta.persistence.ManyToMany
import jakarta.persistence.ManyToOne
import ru.kazantsev.sportiksmonitor.data.model.AbstractEntity

@Entity
class Installation protected constructor() : AbstractEntity() {

    lateinit var protocol: String
    //TODO сделать уникальным
    lateinit var host: String
    lateinit var accessKey: String
    var appVersion: String? = null
    var groovyVersion: String? = null
    var archived: Boolean = false
    //var backupConfigWhileMigration = true

    @ManyToOne
    var lastFromMigrationLog: MigrationLog? = null

    @ManyToOne
    var lastToMigrationLog: MigrationLog? = null

    constructor(
        protocol: String,
        host: String,
        accessKey: String
    ) : this() {
        this.protocol = protocol
        this.host = host
        this.accessKey = accessKey
    }

}