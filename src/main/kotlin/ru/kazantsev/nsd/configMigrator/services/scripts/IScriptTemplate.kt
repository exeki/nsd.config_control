package ru.kazantsev.nsd.configMigrator.services.scripts

interface IScriptTemplate {
    fun getScriptContent() : String
}