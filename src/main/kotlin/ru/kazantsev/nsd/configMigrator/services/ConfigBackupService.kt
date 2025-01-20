package ru.kazantsev.nsd.configMigrator.services

import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import ru.kazantsev.nsd.configMigrator.data.model.ConfigBackup
import ru.kazantsev.nsd.configMigrator.data.model.Installation
import ru.kazantsev.nsd.configMigrator.data.model.enums.ConfigBackupType
import ru.kazantsev.nsd.configMigrator.data.repo.ConfigBackupRepo
import java.time.format.DateTimeFormatter


@Service
class ConfigBackupService (
    val connectorService: ConnectorService,
    val configBackupRepo: ConfigBackupRepo
) {

    val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy-HH;mm;ss")

    @Transactional
    fun fetchAndCreateBackup(inst : Installation, type : ConfigBackupType): ConfigBackup {
        val con = connectorService.getConnectorForInstallation(inst)
        val conf = con.metainfo()
        val configBackup = ConfigBackup(inst, type, conf)
        return configBackupRepo.save(configBackup)
    }

}