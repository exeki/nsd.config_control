package ru.kazantsev.nsd.configMigrator

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.ConfigurableApplicationContext

@SpringBootApplication
class NsdConfigMigrator {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            appContext = runApplication<NsdConfigMigrator>(*args)
        }

        @JvmStatic
        lateinit var appContext : ConfigurableApplicationContext
    }
}


