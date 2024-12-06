package ru.kazantsev.nsd.configMigrator.data.repo

import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.PagingAndSortingRepository
import ru.kazantsev.nsd.configMigrator.data.model.ConfigBackup

interface ConfigBackupRepo : PagingAndSortingRepository<ConfigBackup, Long>, CrudRepository<ConfigBackup, Long>