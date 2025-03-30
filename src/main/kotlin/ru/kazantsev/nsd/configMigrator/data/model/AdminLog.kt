package ru.kazantsev.nsd.configMigrator.data.model

import jakarta.persistence.*
import ru.kazantsev.nsd.configMigrator.data.dto.MetainfoImportStartLogDto

/**
 * Хранит данные из админ лога NSD
 */
@Entity
class AdminLog() : AbstractEntity() {
    lateinit var uuid: String
    lateinit var actionType: String
    lateinit var actionDate: String
    lateinit var authorLogin: String
    lateinit var authorIP: String
    lateinit var category: String
    lateinit var categoryName: String

    @Lob
    @Basic(fetch = FetchType.LAZY)
    lateinit var description: String

    @ManyToOne(fetch = FetchType.LAZY)
    lateinit var migrationLog: MigrationLog

    constructor(data : MetainfoImportStartLogDto.AdminLogData, migrationLog : MigrationLog) : this() {
        this.uuid = data.UUID
        this.actionType = data.actionType
        this.actionDate = data.actionDate
        this.authorLogin = data.authorLogin
        this.authorIP = data.authorIP
        this.category = data.category
        this.categoryName = data.categoryName
        this.description = data.description
        this.migrationLog = migrationLog
    }
}