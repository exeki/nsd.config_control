package ru.kazantsev.nsd.configMigrator.data.repo

import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.PagingAndSortingRepository
import ru.kazantsev.nsd.configMigrator.data.model.InstallationGroup

interface InstallationGroupRepo : PagingAndSortingRepository<InstallationGroup, Long>, CrudRepository<InstallationGroup, Long> {
    fun findByArchivedIs(archived: Boolean): List<InstallationGroup>
}