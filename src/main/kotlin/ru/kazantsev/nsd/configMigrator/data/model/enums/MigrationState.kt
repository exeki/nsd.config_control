package ru.kazantsev.nsd.configMigrator.data.model.enums

import com.fasterxml.jackson.annotation.JsonValue

enum class MigrationState (@JsonValue val code : String) {
    IN_PROGRESS ("inProgress"),
    COMPLETED("completed"),
    LOST_PROCESS("lostProcess"),
    ERROR("error");
}