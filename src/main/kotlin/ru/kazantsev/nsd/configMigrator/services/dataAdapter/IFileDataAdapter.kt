package ru.kazantsev.nsd.configMigrator.services.dataAdapter

interface IFileDataAdapter {
    fun save(data : String, fileName : String): String

    fun delete(id : String)

    fun getDownloadLink(id : String): String

    fun getXmlFile(id : String): Array<Byte>
}