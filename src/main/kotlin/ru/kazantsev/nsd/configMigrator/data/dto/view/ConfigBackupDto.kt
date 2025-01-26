package ru.kazantsev.nsd.configMigrator.data.dto.view

import jakarta.persistence.*
import ru.kazantsev.nsd.configMigrator.data.model.ConfigBackup
import ru.kazantsev.nsd.configMigrator.data.model.Installation
import ru.kazantsev.nsd.configMigrator.data.model.enums.ConfigBackupType
import ru.kazantsev.nsd.configMigrator.services.enum_converter.ConfigBackupTypeConverter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ConfigBackupDto(val configBackup: ConfigBackup) {

    val installation: Installation
        get() = configBackup.installation

    val type: ConfigBackupType
        get() = configBackup.type


    val title: String
        get() = configBackup.title


    val createdDate: LocalDateTime
        get() = configBackup.createdDate


    val id: Long?
        get() = configBackup.id


}