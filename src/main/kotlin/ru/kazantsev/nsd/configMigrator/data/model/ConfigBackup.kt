package ru.kazantsev.nsd.configMigrator.data.model

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import ru.kazantsev.nsd.configMigrator.data.model.enums.ConfigBackupType
import ru.kazantsev.nsd.configMigrator.services.enum_converter.ConfigBackupTypeConverter
import java.time.format.DateTimeFormatter

@Entity
class ConfigBackup () : AbstractEntity() {

    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull
    lateinit var installation: Installation
    @Convert(converter = ConfigBackupTypeConverter::class)
    @NotNull
    lateinit var type: ConfigBackupType
    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull
    lateinit var configFile: DBFile
    var note : String? = null
    //Ключевой бекап
    var key : Boolean = false
    val title: String
        get() = installation.host + '_' + createdDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"))

    constructor(installation : Installation, type : ConfigBackupType, configFile: DBFile) : this() {
        this.installation = installation
        this.type = type
        this.configFile = configFile
    }

}