package ru.kazantsev.nsd.configMigrator.data.model.enums

import com.fasterxml.jackson.annotation.JsonValue

enum class ConfigBackupType(@JsonValue val code: String, val title: String) {
    DURING_MIGRATION_FROM("duringMigrationFrom", "При миграции с инсталляции"),
    DURING_MIGRATION_TO("duringMigrationTo", "При миграции на инсталляцию"),
    SCHEDULER("scheduleMigrationFrom", "Планировщик"),
    HAND("hand", "Ручной")
}