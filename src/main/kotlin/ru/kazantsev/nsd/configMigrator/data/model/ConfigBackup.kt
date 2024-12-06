package ru.kazantsev.nsd.configMigrator.data.model

import jakarta.persistence.Entity
import jakarta.persistence.ManyToOne
import ru.kazantsev.nsd.configMigrator.data.model.enums.ConfigBackupType
import ru.kazantsev.sportiksmonitor.data.model.AbstractEntity
import java.time.LocalDateTime

@Entity
 class ConfigBackup protected constructor() : AbstractEntity() {
    @ManyToOne
    lateinit var installation: Installation
    val creationDate: LocalDateTime = LocalDateTime.now()
    lateinit var type: ConfigBackupType
    lateinit var downloadLink : String
    lateinit var fileId : String

    constructor(
        inst : Installation,
        type: ConfigBackupType,
        downloadLink : String,
        fileId : String
    ) : this() {
        this.installation = inst
        this.type = type
        this.downloadLink = downloadLink
        this.fileId = fileId
    }
}