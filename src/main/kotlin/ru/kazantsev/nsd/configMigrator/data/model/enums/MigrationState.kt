package ru.kazantsev.nsd.configMigrator.data.model.enums

import com.fasterxml.jackson.annotation.JsonValue

enum class MigrationState(
    @JsonValue val code: String,
    val title: String,
    val color: String
) {
    SENT("sent", "Отправлена", ""),
    STARTED("started", "Начата", ""),
    DONE("completed", "Завершена", ""),
    LOST_PROCESS("lostProcess", "Процесс потерян", ""),
    ERROR("error", "Ошибка", "")
}