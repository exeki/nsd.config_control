package ru.kazantsev.nsd.configMigrator.data.model

import jakarta.persistence.Column
import jakarta.persistence.Entity

@Entity
class Installation() : AbstractEntity() {

    var protocol: String = ""

    @Column(unique = true)
    var host: String = ""
    var accessKey: String = ""
    var appVersion: String? = null
    var groovyVersion: String? = null
    var archived: Boolean = false
    //var backupConfigWhileMigration = true

    //TODO передалать не не хранимый атрибут
    //@ManyToOne
    //var lastFromMigrationLog: MigrationLog? = null

    //TODO передалать не не хранимый атрибут
    //@ManyToOne
    //var lastToMigrationLog: MigrationLog? = null

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