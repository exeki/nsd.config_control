package ru.kazantsev.nsd.configMigrator.data.model

import jakarta.persistence.Entity
import jakarta.persistence.ManyToMany
import jakarta.persistence.ManyToOne
import ru.kazantsev.sportiksmonitor.data.model.AbstractEntity

@Entity
class MigrationPath : AbstractEntity() {
    @ManyToOne
    lateinit var from : Installation
    @ManyToOne
    lateinit var to : Installation
    var overrideAll : Boolean = false
    @ManyToOne
    var lastLog : MigrationLog? = null
}