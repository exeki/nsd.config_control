package ru.kazantsev.nsd.configMigrator.data.model

import jakarta.persistence.Entity
import jakarta.validation.constraints.NotBlank

@Entity
class Scheduler : AbstractEntity() {
    //перенос конфигурации
    //бекап скприптов и конфигурации
    @NotBlank
    lateinit var title: String
}