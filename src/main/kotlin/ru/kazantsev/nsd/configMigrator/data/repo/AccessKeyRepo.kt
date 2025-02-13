package ru.kazantsev.nsd.configMigrator.data.repo

import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.PagingAndSortingRepository
import ru.kazantsev.nsd.configMigrator.data.model.AccessKey
import ru.kazantsev.nsd.configMigrator.data.model.Installation
import ru.kazantsev.nsd.configMigrator.data.model.User

interface AccessKeyRepo : PagingAndSortingRepository<AccessKey, Long>, CrudRepository<AccessKey, Long> {
    fun findByUser(user: User): List<AccessKey>
    fun findByUserAndInstallation(user: User, install: Installation): List<AccessKey>
}