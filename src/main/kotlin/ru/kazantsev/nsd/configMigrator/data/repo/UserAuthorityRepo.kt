package ru.kazantsev.nsd.configMigrator.data.repo

import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.PagingAndSortingRepository
import ru.kazantsev.nsd.configMigrator.data.model.UserAuthority
import java.util.*

interface UserAuthorityRepo : PagingAndSortingRepository<UserAuthority, Long>, CrudRepository<UserAuthority, Long> {
    fun findByCode(code: String): Optional<UserAuthority>
}