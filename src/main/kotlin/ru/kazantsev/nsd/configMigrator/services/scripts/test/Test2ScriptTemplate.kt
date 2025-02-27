package ru.kazantsev.nsd.configMigrator.services.scripts.test
import org.springframework.util.ResourceUtils
import ru.kazantsev.nsd.configMigrator.services.scripts.IScriptTemplate

class Test2ScriptTemplate (private val dateStr : String) : IScriptTemplate {
    override fun getScriptContent(): String {
        val str = ResourceUtils.getFile("classpath:scripts/test/test2.groovy").readText(Charsets.UTF_8)
        return str.replace("%date", dateStr)
    }
}