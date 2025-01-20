package ru.kazantsev.nsd.configMigrator.services

import jakarta.transaction.Transactional
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import ru.kazantsev.nsd.configMigrator.data.model.Installation
import ru.kazantsev.nsd.configMigrator.data.model.MigrationLog
import ru.kazantsev.nsd.configMigrator.data.model.MigrationPath
import ru.kazantsev.nsd.configMigrator.data.model.enums.ConfigBackupType
import ru.kazantsev.nsd.configMigrator.data.model.enums.MigrationState
import ru.kazantsev.nsd.configMigrator.data.repo.InstallationRepo
import ru.kazantsev.nsd.configMigrator.data.repo.MigrationLogRepo
import ru.kazantsev.nsd.configMigrator.data.repo.MigrationPathRepo
import java.net.SocketTimeoutException

@Service
class InstallationService(
    val installationRepo: InstallationRepo,
    val connectorService: ConnectorService,
    val migrationLogRepo: MigrationLogRepo,
    val cbService: ConfigBackupService,
    val migrationPathRepo: MigrationPathRepo
) {

    val logger: Logger = LoggerFactory.getLogger(InstallationService::class.java)

    @Transactional
    fun updateInstallation(inst: Installation): Installation {
        val con = connectorService.getConnectorForInstallation(inst)
        inst.appVersion = con.version()
        inst.groovyVersion = con.groovyVersion()
        return installationRepo.save(inst)
    }

    @Transactional
    fun startMigration(
        from: Installation,
        to: Installation,
        overrideAll: Boolean,
        fromBackup: Boolean,
        toBackup: Boolean
    ): MigrationLog {
        val log = MigrationLog(from, to, overrideAll)
        from.lastFromMigrationLog = log
        to.lastToMigrationLog = log
        try {
            val con = connectorService.getConnectorForInstallation(from)
            var config : String? = null
            if (fromBackup) {
                val a  = cbService.fetchAndCreateBackup(from, ConfigBackupType.DURING_MIGRATION_FROM)
                log.fromBackup = a
                config = a.configFileContent
            }
            else config = con.metainfo()
            if (toBackup) log.toBackup = cbService.fetchAndCreateBackup(to, ConfigBackupType.DURING_MIGRATION_TO)
            try {
                con.uploadMetainfo(config, 1000)
            } catch (ignored: SocketTimeoutException) {
                logger.info("Словил SocketTimeoutException при отправке метаинфы. This is fine...")
            }
            log.state = MigrationState.IN_PROGRESS
        } catch (e: Exception) {
            log.state = MigrationState.ERROR
            log.errorText = e.message
        }
        migrationLogRepo.save(log)
        installationRepo.save(from)
        installationRepo.save(to)
        return log
    }

    @Transactional
    fun startMigration(migrationPath: MigrationPath): MigrationLog {
        val log = startMigration(
            migrationPath.from,
            migrationPath.to,
            migrationPath.overrideAll,
            migrationPath.fromBackup,
            migrationPath.toBackup
        )
        migrationPath.lastLog = log
        migrationPathRepo.save(migrationPath)
        return log
    }
}