package ru.kazantsev.nsd.configMigrator.services

import org.springframework.stereotype.Service
import ru.kazantsev.nsd.configMigrator.data.model.Installation
import ru.kazantsev.nsd.configMigrator.data.model.User
import ru.kazantsev.nsd.configMigrator.services.scripts.IScriptTemplate

@Service
class ScriptExecutionService(val connectorService: ConnectorService) {

    fun executeScript( template : IScriptTemplate, installation : Installation, user : User) : String {
        val con = connectorService.getConnectorForInstallation(installation, user)
        return con.execFile(template.getScriptContent())
    }

}