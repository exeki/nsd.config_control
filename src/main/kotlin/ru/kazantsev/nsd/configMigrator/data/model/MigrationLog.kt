package ru.kazantsev.nsd.configMigrator.data.model

import jakarta.persistence.Entity
import jakarta.persistence.ManyToOne
import ru.kazantsev.nsd.configMigrator.data.model.enums.MigrationState
import ru.kazantsev.sportiksmonitor.data.model.AbstractEntity

@Entity
 open class MigrationLog protected constructor() : AbstractEntity(){
    @ManyToOne
    lateinit var from: Installation
    @ManyToOne
    var fromBackup : ConfigBackup? = null
    @ManyToOne
    lateinit var to: Installation
    @ManyToOne
    var toBackup : ConfigBackup? = null

    var overrideAll : Boolean = false

    var state : MigrationState = MigrationState.IN_PROGRESS

    var errorText : String? = null

    constructor(from: Installation, to: Installation, overrideAll : Boolean) : this() {
        this.from = from
        this.to = to
        this.overrideAll = overrideAll
    }

    constructor(migrationPath: MigrationPath) : this(
        migrationPath.from,
        migrationPath.to,
        migrationPath.overrideAll
    )

}