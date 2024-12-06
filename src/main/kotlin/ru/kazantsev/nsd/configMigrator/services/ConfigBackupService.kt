package ru.kazantsev.nsd.configMigrator.services

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import ru.kazantsev.nsd.configMigrator.NsdConfigMigrator
import ru.kazantsev.nsd.configMigrator.config.AppConfig
import ru.kazantsev.nsd.configMigrator.data.model.ConfigBackup
import ru.kazantsev.nsd.configMigrator.data.model.Installation
import ru.kazantsev.nsd.configMigrator.data.model.enums.ConfigBackupType
import ru.kazantsev.nsd.configMigrator.data.repo.ConfigBackupRepo
import ru.kazantsev.nsd.configMigrator.services.dataAdapter.IFileDataAdapter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


@Service
class ConfigBackupService (
    val connectorService: ConnectorService,
    val configBackupRepo: ConfigBackupRepo,
    val fileDataAdapter : IFileDataAdapter,
) {

    val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy-HH;mm;ss")

    @Transactional
    fun fetchAndCreateBackup(inst : Installation, type : ConfigBackupType): ConfigBackup {
        val con = connectorService.getConnectorForInstallation(inst)
        val conf = con.metainfo()
        return createBackup(inst, conf, type)
    }


    @Transactional
    fun createBackup(inst : Installation, xmlConfig: String, type : ConfigBackupType) : ConfigBackup {
        val time = LocalDateTime.now()
        val fileName = inst.host + "_" + time.format(formatter)
        val fileId = fileDataAdapter.save(xmlConfig, fileName)
        val configBackup = ConfigBackup(
            inst,
            type,
            fileId,
            fileDataAdapter.getDownloadLink(fileId)
        )
        return configBackupRepo.save(configBackup)
    }

}