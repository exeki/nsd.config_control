package ru.kazantsev.nsd.configMigrator.data.repo

import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository
import ru.kazantsev.nsd.configMigrator.data.model.DBFile

@Repository
interface DBFileRepo : PagingAndSortingRepository<DBFile, Long>, CrudRepository<DBFile, Long>