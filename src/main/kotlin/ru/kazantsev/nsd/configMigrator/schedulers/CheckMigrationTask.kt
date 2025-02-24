package ru.kazantsev.nsd.configMigrator.schedulers

import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class CheckMigrationTask {
    private val log = LoggerFactory.getLogger(CheckMigrationTask::class.java)

    @Scheduled(fixedRate = 60000)
    fun scheduledTask() {
        //TODO проверка состояний задач либо запросом по стандартному API (adminLog), либо при помощи выполнения скрипта через ScriptExecutionService
    }
}