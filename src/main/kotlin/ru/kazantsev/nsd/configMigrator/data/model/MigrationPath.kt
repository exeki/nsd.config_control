package ru.kazantsev.nsd.configMigrator.data.model

import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.ManyToOne

@Entity
class MigrationPath constructor() : AbstractEntity() {
    @ManyToOne(fetch = FetchType.LAZY)
    lateinit var from: Installation
    var fromBackup: Boolean = false

    @ManyToOne(fetch = FetchType.LAZY)
    lateinit var to: Installation
    var toBackup: Boolean = false
    var overrideAll: Boolean = false

    @ManyToOne(fetch = FetchType.LAZY)
    var lastLog: MigrationLog? = null

    constructor(from: Installation, to: Installation, overrideAll: Boolean = false) : this() {
        this.from = from
        this.to = to
        this.overrideAll = overrideAll
    }
}