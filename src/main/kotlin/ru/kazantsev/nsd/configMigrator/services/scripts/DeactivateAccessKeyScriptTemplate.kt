package ru.kazantsev.nsd.configMigrator.services.scripts

import org.springframework.util.ResourceUtils

class DeactivateAccessKeyScriptTemplate(private val accessKey: String) : IScriptTemplate {
    override fun getScriptContent(): String {
        val str = ResourceUtils.getFile("classpath:scripts/deactivateAccessKey.groovy").readText(Charsets.UTF_8)
        return str.replace("%accessKey", accessKey)
    }
}