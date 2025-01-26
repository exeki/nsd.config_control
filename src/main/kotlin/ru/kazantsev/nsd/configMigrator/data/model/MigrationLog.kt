package ru.kazantsev.nsd.configMigrator.data.model

import jakarta.persistence.Convert
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.ManyToOne
import ru.kazantsev.nsd.configMigrator.data.model.enums.MigrationState
import ru.kazantsev.nsd.configMigrator.services.enum_converter.MigrationStateConverter

@Entity
class MigrationLog constructor() : AbstractEntity() {
    @ManyToOne(fetch = FetchType.LAZY)
    lateinit var from: Installation

    @ManyToOne
    var fromBackup: ConfigBackup? = null

    @ManyToOne(fetch = FetchType.LAZY)
    lateinit var to: Installation

    @ManyToOne
    var toBackup: ConfigBackup? = null

    var overrideAll: Boolean = false

    @Convert(converter = MigrationStateConverter::class)
    var state: MigrationState = MigrationState.IN_PROGRESS

    var errorText: String? = null

    constructor(from: Installation, to: Installation, overrideAll: Boolean) : this() {
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