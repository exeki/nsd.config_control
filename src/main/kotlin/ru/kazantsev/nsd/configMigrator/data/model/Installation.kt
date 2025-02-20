package ru.kazantsev.nsd.configMigrator.data.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.ManyToMany
import jakarta.validation.constraints.NotBlank

@Entity
class Installation() : AbstractEntity() {

    @NotBlank
    var protocol: String = ""
    @Column(unique = true)
    @NotBlank
    var host: String = ""
    var appVersion: String? = null
    var groovyVersion: String? = null
    var important : Boolean = false

    @ManyToMany(fetch = FetchType.LAZY)
    var groups : MutableSet<InstallationGroup> = mutableSetOf()

    constructor(
        protocol: String,
        host: String,
        //accessKey: String
    ) : this() {
        this.protocol = protocol
        this.host = host
        //this.accessKey = accessKey
    }

}