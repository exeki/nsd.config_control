package ru.kazantsev.nsd.configMigrator.data.model

import jakarta.persistence.Entity
import jakarta.persistence.ManyToMany
import jakarta.persistence.ManyToOne
import ru.kazantsev.sportiksmonitor.data.model.AbstractEntity

@Entity
open class MigrationPath protected constructor() : AbstractEntity() {
    @ManyToOne
    lateinit var from : Installation
    var fromBackup: Boolean = false
    @ManyToOne
    lateinit var to : Installation
    var toBackup: Boolean = false
    var overrideAll : Boolean = false
    @ManyToOne
    var lastLog : MigrationLog? = null

    constructor(from: Installation, to: Installation, overrideAll: Boolean = false) : this() {
        this.from = from
        this.to = to
        this.overrideAll = overrideAll
    }
}