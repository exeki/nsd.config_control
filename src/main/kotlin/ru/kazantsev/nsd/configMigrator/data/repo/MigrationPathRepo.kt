package ru.kazantsev.nsd.configMigrator.data.repo

import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.PagingAndSortingRepository
import ru.kazantsev.nsd.configMigrator.data.model.MigrationPath

interface MigrationPathRepo : PagingAndSortingRepository<MigrationPath, Long>, CrudRepository<MigrationPath, Long>