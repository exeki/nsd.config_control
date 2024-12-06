package ru.kazantsev.nsd.configMigrator.data.repo

import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.PagingAndSortingRepository
import ru.kazantsev.nsd.configMigrator.data.model.Installation

interface InstallationRepo : PagingAndSortingRepository<ru.kazantsev.nsd.configMigrator.data.model.Installation, Long>, CrudRepository<ru.kazantsev.nsd.configMigrator.data.model.Installation, Long>