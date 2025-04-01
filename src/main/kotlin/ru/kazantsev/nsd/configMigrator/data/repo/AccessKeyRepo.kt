package ru.kazantsev.nsd.configMigrator.data.repo

import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.PagingAndSortingRepository
import ru.kazantsev.nsd.configMigrator.data.model.AccessKey
import ru.kazantsev.nsd.configMigrator.data.model.Installation
import ru.kazantsev.nsd.configMigrator.data.model.User
import java.time.LocalDateTime

interface AccessKeyRepo : PagingAndSortingRepository<AccessKey, Long>, CrudRepository<AccessKey, Long> {
    fun findByUser(user: User): List<AccessKey>
    fun findByUserAndInstallationAndExpiredIsFalse(user: User, install: Installation):  List<AccessKey>
    fun findByDateIsBefore(date: LocalDateTime): List<AccessKey>
    fun findByDateIsBeforeAndExpiredIs(date: LocalDateTime, expired : Boolean): List<AccessKey>
}