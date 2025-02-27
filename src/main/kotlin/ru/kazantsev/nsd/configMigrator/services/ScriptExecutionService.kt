package ru.kazantsev.nsd.configMigrator.services

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import ru.kazantsev.nsd.configMigrator.data.model.Installation
import ru.kazantsev.nsd.configMigrator.data.model.User
import ru.kazantsev.nsd.configMigrator.services.scripts.IScriptTemplate

@Service
class ScriptExecutionService(
    private val connectorService: ConnectorService,
    private val objectMapper: ObjectMapper,
) {

    private val log = LoggerFactory.getLogger(ScriptExecutionService::class.java)

    fun executeScript(template: IScriptTemplate, installation: Installation, user: User): String {
        val con = connectorService.getConnectorForInstallation(installation, user)
        val script = template.getScriptContent()
        log.info(script)
        return con.execFile(script)
    }

    fun <T> executeScriptAndRead(
        template: IScriptTemplate,
        installation: Installation,
        user: User,
        valueType: Class<T> 
    ): T {
        val res = executeScript(template, installation, user)
        return objectMapper.readValue(res, valueType)
    }

}