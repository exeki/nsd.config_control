package ru.kazantsev.nsd.configMigrator.schedulers

import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import ru.kazantsev.nsd.configMigrator.data.repo.AccessKeyRepo
import java.time.LocalDateTime

@Service
class ExpiredAccessKeys (
    val accessKeyRepo: AccessKeyRepo
) {
    private val log = LoggerFactory.getLogger(ExpiredAccessKeys::class.java)

    @Scheduled(fixedRate = 60000)
    fun scheduledTask() {
        log.info("Задача выполняется")
        accessKeyRepo.findByDateIsBefore(LocalDateTime.now()).forEach{
            it.expired = true
            log.info("Ключ ${it.id} пользователя ${it.user.fullName} просрочен")
            accessKeyRepo.save(it)
        }
        log.info("Задача выполнена")
    }
}