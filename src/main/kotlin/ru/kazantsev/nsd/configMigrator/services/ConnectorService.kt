package ru.kazantsev.nsd.configMigrator.services

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Service
import ru.kazantsev.nsd.basic_api_connector.Connector
import ru.kazantsev.nsd.basic_api_connector.ConnectorParams
import ru.kazantsev.nsd.configMigrator.data.model.Installation

@Service
class ConnectorService(val objectMapper: ObjectMapper) {

    protected val connectorMap = mutableMapOf<String, Connector>()

    fun getConnectorParamsForInstallation(installation: Installation): ConnectorParams {
        return ConnectorParams(
            installation.host,
            installation.protocol,
            installation.host,
            installation.accessKey,
            true
        )
    }

    fun getConnectorForInstallation(installation: Installation): Connector {
        val params = getConnectorParamsForInstallation(installation)
        val con = Connector(params)
        con.setObjectMapper(objectMapper)
        connectorMap[installation.host] = con
        return con
    }

    fun getConnectorForInstallationOld(installation: Installation): Connector {
        var con = connectorMap[installation.host]
        if (con != null) return con
        else {
            val params = getConnectorParamsForInstallation(installation)
            con = Connector(params)
            con.setObjectMapper(objectMapper)
            connectorMap[installation.host] = con
            return con
        }
    }
}