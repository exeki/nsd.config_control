package ru.kazantsev.nsd.configMigrator.data.model

import jakarta.persistence.Entity

@Entity
class Scheduler : AbstractEntity() {
    //перенос конфигурации
    //бекап скприптов и конфигурации
    lateinit var title: String
}