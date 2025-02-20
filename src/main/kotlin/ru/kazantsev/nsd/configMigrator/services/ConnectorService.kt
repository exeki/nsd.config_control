package ru.kazantsev.nsd.configMigrator.services

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Service
import ru.kazantsev.nsd.basic_api_connector.Connector
import ru.kazantsev.nsd.basic_api_connector.ConnectorParams
import ru.kazantsev.nsd.configMigrator.data.model.Installation
import ru.kazantsev.nsd.configMigrator.data.model.User
import ru.kazantsev.nsd.configMigrator.data.repo.AccessKeyRepo
import ru.kazantsev.nsd.configMigrator.exception.NoSuitableAccessKeyException

@Service
class ConnectorService(
    val objectMapper: ObjectMapper,
    val accessKeyRepo: AccessKeyRepo
) {

    protected val connectorMap = mutableMapOf<String, Connector>()

    fun getConnectorParamsForInstallation(installation: Installation, user: User): ConnectorParams {

        val keys = accessKeyRepo.findByUserAndInstallationAndExpiredIsFalse(user, installation)
        if(keys.isEmpty()) throw NoSuitableAccessKeyException("Не удалось найти ключ пользователя ${user.username} для инсталляции ${installation.host}")
        return ConnectorParams(
            installation.host,
            installation.protocol,
            installation.host,
            keys.last().accessKey,
            true
        )
    }

    fun getConnectorParamsForInstallation(installation: Installation): ConnectorParams {
        return ConnectorParams(
            installation.host,
            installation.protocol,
            installation.host,
            null,
            true
        )
    }

    fun getConnectorForInstallation(installation: Installation, user : User): Connector {
        val params = getConnectorParamsForInstallation(installation, user)
        val con = Connector(params)
        con.setObjectMapper(objectMapper)
        connectorMap[installation.host] = con
        return con
    }


    fun getConnectorForInstallation(installation: Installation): Connector {
        val params = getConnectorParamsForInstallation(installation)
        val con = Connector(params)
        con.setObjectMapper(objectMapper)
        connectorMap[installation.host] = con
        return con
    }
}