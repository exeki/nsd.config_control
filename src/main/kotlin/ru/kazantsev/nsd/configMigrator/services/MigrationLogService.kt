package ru.kazantsev.nsd.configMigrator.services

import org.springframework.stereotype.Service
import ru.kazantsev.nsd.configMigrator.data.model.MigrationLog
import ru.kazantsev.nsd.configMigrator.data.model.User
import ru.kazantsev.nsd.configMigrator.data.repo.MigrationLogRepo
import ru.kazantsev.nsd.configMigrator.exception.MissingUserException
import ru.kazantsev.nsd.configMigrator.services.scripts.GetMetainfoImportStartLogScriptTemplate
import java.time.format.DateTimeFormatter

@Service
class MigrationLogService(
    private val migrationLogRepo: MigrationLogRepo,
    private val scriptExecutionService: ScriptExecutionService
) {
    companion object {
        private const val DATE_FORMAT = "dd.MM.yyyy HH:mm:ss.SSS"
        private val dateFormat = DateTimeFormatter.ofPattern(DATE_FORMAT)
    }

    fun updateMigrationLog(migrationLog: MigrationLog, user: User? = null): MigrationLog {
        var us = user
        if (user == null) us = migrationLog.user
        if (user == null) throw MissingUserException("Отсутствует подходящий пользователь для запроса состояния миграции")
        scriptExecutionService.executeScriptAndRead(
            GetMetainfoImportStartLogScriptTemplate(migrationLog.createdDate.format(dateFormat)),
            migrationLog.to, us!!,
            HashMap::class.java //TODO сделать дто
        )
        return migrationLog
    }
}