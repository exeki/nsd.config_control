package ru.kazantsev.nsd.configMigrator.data.repo

import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.PagingAndSortingRepository
import ru.kazantsev.nsd.configMigrator.data.model.User
import java.util.*

interface UserRepo : PagingAndSortingRepository<User, Long>, CrudRepository<User, Long> {
    fun findByUsername(username: String): Optional<User>
    fun findByArchivedIs(archived: Boolean): MutableList<User>
}