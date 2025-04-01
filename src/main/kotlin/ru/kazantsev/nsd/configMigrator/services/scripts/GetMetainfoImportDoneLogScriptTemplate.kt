package ru.kazantsev.nsd.configMigrator.services.scripts

import org.springframework.util.ResourceUtils

class GetMetainfoImportDoneLogScriptTemplate(private val date : String) : IScriptTemplate {
    override fun getScriptContent(): String {
        val str = ResourceUtils.getFile("classpath:scripts/getMetainfoImportDoneLog.groovy").readText(Charsets.UTF_8)
        return str.replace("%date", date)
    }
}