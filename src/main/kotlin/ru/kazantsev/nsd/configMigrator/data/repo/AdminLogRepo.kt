package ru.kazantsev.nsd.configMigrator.data.repo

import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.PagingAndSortingRepository
import ru.kazantsev.nsd.configMigrator.data.model.*
import java.time.LocalDateTime

interface AdminLogRepo : PagingAndSortingRepository<AdminLog, Long>, CrudRepository<AdminLog, Long> {
    fun findByMigrationLog(migrationLog: MigrationLog) : List<AdminLog>
}