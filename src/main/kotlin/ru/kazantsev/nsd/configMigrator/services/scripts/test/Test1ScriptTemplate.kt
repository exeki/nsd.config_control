package ru.kazantsev.nsd.configMigrator.services.scripts.test
import org.springframework.util.ResourceUtils
import ru.kazantsev.nsd.configMigrator.services.scripts.IScriptTemplate

class Test1ScriptTemplate : IScriptTemplate {
    override fun getScriptContent(): String {
        return ResourceUtils.getFile("classpath:scripts/test/test1.groovy").readText(Charsets.UTF_8)
    }
}