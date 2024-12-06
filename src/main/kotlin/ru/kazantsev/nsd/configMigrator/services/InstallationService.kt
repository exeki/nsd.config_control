package ru.kazantsev.nsd.configMigrator.services

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.transaction.Transactional
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import ru.kazantsev.nsd.basic_api_connector.Connector
import ru.kazantsev.nsd.basic_api_connector.ConnectorParams
import ru.kazantsev.nsd.configMigrator.data.model.Installation
import ru.kazantsev.nsd.configMigrator.data.model.MigrationLog
import ru.kazantsev.nsd.configMigrator.data.model.MigrationPath
import ru.kazantsev.nsd.configMigrator.data.model.enums.ConfigBackupType
import ru.kazantsev.nsd.configMigrator.data.model.enums.MigrationState
import ru.kazantsev.nsd.configMigrator.data.repo.InstallationRepo
import ru.kazantsev.nsd.configMigrator.data.repo.MigrationLogRepo
import ru.kazantsev.nsd.configMigrator.data.repo.MigrationPathRepo
import ru.kazantsev.nsd.configMigrator.services.dataAdapter.IFileDataAdapter
import ru.kazantsev.nsd.configMigrator.services.dataAdapter.PostgresSqlFileDataAdapter
import java.net.SocketException
import java.net.SocketTimeoutException

@Service
class InstallationService(
    val installationRepo: InstallationRepo,
    val connectorService: ConnectorService,
    val migrationLogRepo: MigrationLogRepo,
    val configBackupService: ConfigBackupService,
    val fileDataAdapter: IFileDataAdapter,
    val migrationPathRepo: MigrationPathRepo
) {

    val logger : Logger = LoggerFactory.getLogger(InstallationService::class.java)

    fun addNewInstallation(protocol: String, host: String, accessKey: String): Installation {
        val installation = Installation(protocol, host, accessKey)
        return updateInstallation(installation)
    }

    fun updateInstallation(inst : Installation) : Installation {
        val con = connectorService.getConnectorForInstallation(inst)
        inst.appVersion = con.version()
        inst.groovyVersion = con.groovyVersion()
        return installationRepo.save(inst)
    }

    fun removeInstallation(installation: Installation): Installation {
        installation.archived = true
        return installationRepo.save(installation)
    }

    fun resurrectInstallation(installation: Installation): Installation {
        installation.archived = false
        return installationRepo.save(installation)
    }

    @Transactional
    fun startMigration(from: Installation, to: Installation, overrideAll: Boolean) : MigrationLog {
        val log = MigrationLog(from, to, overrideAll)
        from.lastFromMigrationLog = log
        to.lastToMigrationLog = log
        try {
            val con = connectorService.getConnectorForInstallation(from)
            val config = con.metainfo()
            if (from.backupConfigWhileMigration) log.fromBackup =
                configBackupService.createBackup(from, config , ConfigBackupType.DURING_MIGRATION_FROM)
            if (to.backupConfigWhileMigration) log.toBackup =
                configBackupService.fetchAndCreateBackup(to, ConfigBackupType.DURING_MIGRATION_TO)
            try {
                con.uploadMetainfo(config, 1000)
            } catch (ignored : SocketTimeoutException) {
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
    fun startMigration(migrationPath : MigrationPath) : MigrationLog {
        val log = startMigration(migrationPath.from, migrationPath.to, migrationPath.overrideAll)
        migrationPath.lastLog = log
        migrationPathRepo.save(migrationPath)
        return log
    }
}