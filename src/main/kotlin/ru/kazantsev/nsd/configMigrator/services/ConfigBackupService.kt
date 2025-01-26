package ru.kazantsev.nsd.configMigrator.services

import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import ru.kazantsev.nsd.configMigrator.data.model.ConfigBackup
import ru.kazantsev.nsd.configMigrator.data.model.DBFile
import ru.kazantsev.nsd.configMigrator.data.model.Installation
import ru.kazantsev.nsd.configMigrator.data.model.enums.ConfigBackupType
import ru.kazantsev.nsd.configMigrator.data.repo.ConfigBackupRepo
import ru.kazantsev.nsd.configMigrator.data.repo.DBFileRepo
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


@Service
class ConfigBackupService(
    val connectorService: ConnectorService,
    val configBackupRepo: ConfigBackupRepo,
    val dbFileRepo: DBFileRepo
) {

    fun getNameForBackup(inst: Installation): String {
        return inst.host + '_' + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"))
    }

    @Transactional
    fun fetchAndCreateBackup(inst: Installation, type: ConfigBackupType): ConfigBackup {
        val con = connectorService.getConnectorForInstallation(inst)
        val conf = con.metainfo()
        val file = dbFileRepo.save(DBFile(getNameForBackup(inst), "application/xml", conf.toByteArray()))
        return configBackupRepo.save(ConfigBackup(inst, type, file))
    }

}