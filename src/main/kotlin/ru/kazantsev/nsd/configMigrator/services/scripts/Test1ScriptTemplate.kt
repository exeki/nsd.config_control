package ru.kazantsev.nsd.configMigrator.services.scripts
import org.springframework.util.ResourceUtils

class Test1ScriptTemplate : IScriptTemplate {
    override fun getScriptContent(): String {
        return ResourceUtils.getFile("classpath:scripts/test.groovy").readText(Charsets.UTF_8)
    }
}