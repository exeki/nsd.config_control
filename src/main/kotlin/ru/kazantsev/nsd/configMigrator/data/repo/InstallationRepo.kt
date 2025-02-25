package ru.kazantsev.nsd.configMigrator.data.repo

import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.PagingAndSortingRepository
import ru.kazantsev.nsd.configMigrator.data.model.Installation
import ru.kazantsev.nsd.configMigrator.data.model.InstallationGroup
import java.util.*

interface InstallationRepo : PagingAndSortingRepository<Installation, Long>, CrudRepository<Installation, Long> {
    fun findByArchivedIs(archived: Boolean): List<Installation>
    fun findByArchivedIsAndGroupsIn(archived: Boolean, groups: MutableSet<InstallationGroup>): List<Installation>
    fun findByHostLike(host: String): List<Installation>
    fun findByHost(host : String) : Optional<Installation>
    fun findByHostLikeAndArchivedIs(host: String, archived: Boolean): List<Installation>
    fun findByHostLikeAndArchivedIsAndGroupsIn(
        host: String,
        archived: Boolean,
        groups: MutableSet<InstallationGroup>
    ): List<Installation>
    fun countByArchivedIs(archived: Boolean): Long
}