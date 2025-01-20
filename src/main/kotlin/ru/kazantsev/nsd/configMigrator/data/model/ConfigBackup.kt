package ru.kazantsev.nsd.configMigrator.data.model

import jakarta.persistence.Entity
import jakarta.persistence.ManyToOne
import ru.kazantsev.nsd.configMigrator.data.model.enums.ConfigBackupType
import ru.kazantsev.sportiksmonitor.data.model.AbstractEntity
import java.time.LocalDateTime

@Entity
open class ConfigBackup protected constructor() : AbstractEntity() {

    @ManyToOne
    lateinit var installation: Installation
    lateinit var type: ConfigBackupType
    lateinit var configFileContent: String

    constructor(
        inst : Installation,
        type: ConfigBackupType,
        configFileContent : String
    ) : this() {
        this.installation = inst
        this.type = type
        this.configFileContent = configFileContent
    }
}