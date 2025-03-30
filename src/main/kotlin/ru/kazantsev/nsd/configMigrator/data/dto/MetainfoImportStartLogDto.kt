package ru.kazantsev.nsd.configMigrator.data.dto

class MetainfoImportStartLogDto {
    var find: Boolean = false
    var log: AdminLogData? = null

    class AdminLogData {
        var UUID: String = ""
        var actionType: String = ""
        var actionDate: String = ""
        var authorLogin: String = ""
        var authorIP: String = ""
        var category: String = ""
        var categoryName: String = ""
        var description: String = ""
    }
}