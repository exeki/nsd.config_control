package ru.kazantsev.nsd.configMigrator.services.scripts
import org.springframework.util.ResourceUtils

class GetCurrentInstallationTimeScriptTemplate : IScriptTemplate {
    override fun getScriptContent(): String {
        return ResourceUtils.getFile("classpath:scripts/getCurrentInstallationTime.groovy").readText(Charsets.UTF_8)
    }
}