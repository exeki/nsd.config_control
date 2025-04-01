package ru.kazantsev.nsd.configMigrator.schedulers

import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import ru.kazantsev.nsd.configMigrator.data.dto.MetainfoImportStartLogDto
import ru.kazantsev.nsd.configMigrator.data.model.AdminLog
import ru.kazantsev.nsd.configMigrator.data.model.MigrationLog
import ru.kazantsev.nsd.configMigrator.data.model.enums.MigrationState
import ru.kazantsev.nsd.configMigrator.data.repo.AdminLogRepo
import ru.kazantsev.nsd.configMigrator.data.repo.MigrationLogRepo
import ru.kazantsev.nsd.configMigrator.services.ScriptExecutionService
import ru.kazantsev.nsd.configMigrator.services.scripts.GetMetainfoImportStartLogScriptTemplate
import java.time.LocalDateTime

@Service
class CheckMigrationTask(
    val scriptExecutionService: ScriptExecutionService,
    val migrationLogRepo: MigrationLogRepo,
    val adminLogRepo: AdminLogRepo,
) {

    companion object {
        const val WAIT_PROCESS_IN_MINUTES: Long = 60
    }

    private val log = LoggerFactory.getLogger(CheckMigrationTask::class.java)

    @Scheduled(fixedRate = 60000)
    @Transactional
    fun scheduledTask() {
        log.info("Запуск планировщика проверки логов миграции")
        val send = migrationLogRepo.findByState(MigrationState.SENT)
        send.forEach { migrationLog ->
            log.info("Проверка лога отправленной ${migrationLog.id}")
            checkSent(migrationLog)
        }
        val started = migrationLogRepo.findByState(MigrationState.STARTED)
        started.forEach { migrationLog ->
            log.info("Проверка лога начатой миграции ${migrationLog.id}")
            checkStarted(migrationLog)
        }
        log.info("Проверка логов миграции завершена. Проверешено ${started.size + send.size}")
    }

    @Transactional
    fun checkSent(migrationLog: MigrationLog) {
        val data = scriptExecutionService.executeScriptAndRead(
            GetMetainfoImportStartLogScriptTemplate(migrationLog.installationStartTime),
            migrationLog.to,
            migrationLog.user,
            MetainfoImportStartLogDto::class.java
        )
        if (data.log != null) {
            val adminLog = AdminLog(data.log!!, migrationLog)
            adminLogRepo.save(adminLog)
            migrationLog.state = MigrationState.STARTED
        } else {
            migrationLog.state = MigrationState.LOST_PROCESS
            migrationLog.errorText = "Не удалось найти лог начала импорта."
        }
        migrationLogRepo.save(migrationLog)
    }

    @Transactional
    fun checkStarted(migrationLog: MigrationLog) {
        val data = scriptExecutionService.executeScriptAndRead(
            GetMetainfoImportStartLogScriptTemplate(migrationLog.installationStartTime),
            migrationLog.to,
            migrationLog.user,
            MetainfoImportStartLogDto::class.java
        )
        if (data.log != null) {
            val adminLog = AdminLog(data.log!!, migrationLog)
            adminLogRepo.save(adminLog)
            migrationLog.state = MigrationState.DONE
        } else {
            if (migrationLog.createdDate > LocalDateTime.now().plusMinutes(WAIT_PROCESS_IN_MINUTES)) {
                migrationLog.state = MigrationState.LOST_PROCESS
                migrationLog.errorText = "Ожидание лога заверщения процесса более ${WAIT_PROCESS_IN_MINUTES} минут."
            }
        }
        migrationLogRepo.save(migrationLog)
    }
}