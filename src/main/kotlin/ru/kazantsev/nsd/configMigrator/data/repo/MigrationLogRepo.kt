package ru.kazantsev.nsd.configMigrator.data.repo

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.data.repository.query.Param
import ru.kazantsev.nsd.configMigrator.data.model.Installation
import ru.kazantsev.nsd.configMigrator.data.model.MigrationLog
import ru.kazantsev.nsd.configMigrator.data.model.enums.MigrationState

interface MigrationLogRepo : PagingAndSortingRepository<MigrationLog, Long>, CrudRepository<MigrationLog, Long> {
    @Query("FROM MigrationLog WHERE from = :value OR to = :value")
    fun findByFromOrToIs(@Param("value") inst : Installation) : List<MigrationLog>
    fun findByState(migrationState : MigrationState) : List<MigrationLog>
}