package ru.kazantsev.nsd.configMigrator.data.repo

import jakarta.transaction.Transactional
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.PagingAndSortingRepository
import ru.kazantsev.nsd.configMigrator.data.model.ConfigBackup
import ru.kazantsev.nsd.configMigrator.data.model.Installation

interface ConfigBackupRepo : PagingAndSortingRepository<ConfigBackup, Long>, CrudRepository<ConfigBackup, Long> {
    @Transactional
    fun findByInstallation(inst : Installation) : List<ConfigBackup>
    fun findByInstallationAndKeyIs(installation: Installation, key : Boolean) : List<ConfigBackup>
}