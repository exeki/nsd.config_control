package ru.kazantsev.nsd.configMigrator.services.enum_converter

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import ru.kazantsev.nsd.configMigrator.data.model.enums.ConfigBackupType
import ru.kazantsev.nsd.configMigrator.data.model.enums.MigrationState

@Converter(autoApply = true)
class MigrationStateConverter : AttributeConverter<MigrationState, String> {
    override fun convertToDatabaseColumn(p0: MigrationState?): String? = p0?.code

    override fun convertToEntityAttribute(p0: String?): MigrationState? {
        return MigrationState.entries.find { it.code == p0 }
    }
}