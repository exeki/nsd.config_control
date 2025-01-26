package ru.kazantsev.nsd.configMigrator.services.enum_converter

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import ru.kazantsev.nsd.configMigrator.data.model.enums.ConfigBackupType

@Converter(autoApply = true)
class ConfigBackupTypeConverter : AttributeConverter<ConfigBackupType, String> {
    override fun convertToDatabaseColumn(p0: ConfigBackupType?): String? = p0?.code

    override fun convertToEntityAttribute(p0: String?): ConfigBackupType? {
        return ConfigBackupType.entries.find { it.code == p0 }
    }
}