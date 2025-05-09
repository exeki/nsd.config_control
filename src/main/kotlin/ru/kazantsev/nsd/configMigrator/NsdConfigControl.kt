package ru.kazantsev.nsd.configMigrator

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@EnableScheduling
@SpringBootApplication
class NsdConfigControl {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            runApplication<NsdConfigControl>(*args)
        }
    }
}


