package ru.kazantsev.nsd.configMigrator.services

import jakarta.transaction.Transactional
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import ru.kazantsev.nsd.basic_api_connector.Connector
import ru.kazantsev.nsd.basic_api_connector.ConnectorParams
import ru.kazantsev.nsd.configMigrator.data.model.*
import ru.kazantsev.nsd.configMigrator.data.model.enums.ConfigBackupType
import ru.kazantsev.nsd.configMigrator.data.model.enums.MigrationState
import ru.kazantsev.nsd.configMigrator.data.repo.*
import java.net.SocketTimeoutException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Service
class InstallationService(
    val installationRepo: InstallationRepo,
    val connectorService: ConnectorService,
    val migrationLogRepo: MigrationLogRepo,
    val migrationPathRepo: MigrationPathRepo,
    val configBackupRepo: ConfigBackupRepo,
    val dbFileRepo: DBFileRepo
) {

    val logger: Logger = LoggerFactory.getLogger(InstallationService::class.java)

    @Transactional
    fun updateInstallation(inst: Installation, user : User): Installation {
        val con = connectorService.getConnectorForInstallation(inst, user)
        inst.appVersion = con.version()
        inst.groovyVersion = con.groovyVersion()
        return installationRepo.save(inst)
    }

    fun getNameForBackup(inst: Installation): String {
        return inst.host + '_' + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"))
    }

    @Transactional
    fun fetchAndCreateBackup(inst: Installation, type: ConfigBackupType, user : User): ConfigBackup {
        val con = connectorService.getConnectorForInstallation(inst, user)
        return fetchAndCreateBackup(inst, type, con)
    }

    @Transactional
    fun fetchAndCreateBackup(inst: Installation, type: ConfigBackupType, con : Connector): ConfigBackup {
        val conf = con.metainfo()
        val file = dbFileRepo.save(DBFile(getNameForBackup(inst), "application/xml", conf.toByteArray()))
        return configBackupRepo.save(ConfigBackup(inst, type, file))
    }

    @Transactional
    fun startMigration(
        from: Installation,
        to: Installation,
        overrideAll: Boolean,
        fromBackup: Boolean,
        toBackup: Boolean,
        user : User
    ): MigrationLog {
        val log = MigrationLog(from, to, overrideAll)
        try {
            val toCon = connectorService.getConnectorForInstallation(to, user)
            val fromCon = connectorService.getConnectorForInstallation(from, user)
            val config: String?
            if (fromBackup) {
                log.fromBackup = fetchAndCreateBackup(from, ConfigBackupType.DURING_MIGRATION_FROM, fromCon)
                config = log.fromBackup!!.configFile.getContentAsString()
            } else config = fromCon.metainfo()
            if (toBackup) log.toBackup = fetchAndCreateBackup(to, ConfigBackupType.DURING_MIGRATION_TO, toCon)
            try {
                toCon.uploadMetainfo(config, 1000)
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
    fun startMigration(migrationPath: MigrationPath, user : User): MigrationLog {
        val log = startMigration(
            migrationPath.from,
            migrationPath.to,
            migrationPath.overrideAll,
            migrationPath.fromBackup,
            migrationPath.toBackup,
            user
        )
        migrationPath.lastLog = log
        migrationPathRepo.save(migrationPath)
        return log
    }
}