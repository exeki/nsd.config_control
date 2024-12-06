package ru.kazantsev.nsd.configMigrator.config

import jakarta.annotation.PostConstruct
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import ru.kazantsev.nsd.configMigrator.services.dataAdapter.FileSystemFileAdapterFile
import ru.kazantsev.nsd.configMigrator.services.dataAdapter.IFileDataAdapter

@Configuration
//TODO(внедрить зависимость для работы с конфигурацией)
class AppConfig {
    var fileDataAdapterBeanName: String? = null
    var hostName : String? = null

    //@PostConstruct
    fun postConstruct() {
        if(fileDataAdapterBeanName == null) throw RuntimeException("fileDataAdapterBeanName must not be null")
        if(hostName == null) throw RuntimeException("hostName must be not null")
    }

    @Bean
    //TODO("разные адаптеры в зависимости от конфы")
    fun fileDataAdapter() : IFileDataAdapter {
        return FileSystemFileAdapterFile()
    }
}