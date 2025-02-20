package ru.kazantsev.nsd.configMigrator.init

import jakarta.transaction.Transactional
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component
import ru.kazantsev.nsd.configMigrator.data.model.Installation
import ru.kazantsev.nsd.configMigrator.data.model.InstallationGroup
import ru.kazantsev.nsd.configMigrator.data.repo.InstallationGroupRepo
import ru.kazantsev.nsd.configMigrator.data.repo.InstallationRepo
import ru.kazantsev.nsd.configMigrator.services.InstallationService

@Component
class CreateTestData(
    val installationRepo: InstallationRepo,
    val installationService: InstallationService,
    val installationGroupRepo: InstallationGroupRepo
) : ApplicationRunner {

    @Transactional
    override fun run(args: ApplicationArguments?) {
        createTestGroups()
        createTestInstallations()
    }

    fun createTestGroups() {
        if (installationGroupRepo.count() == 0.toLong()) {
            val group1 = InstallationGroup(
                "GREEN",
                "green"
            )
            installationGroupRepo.save(group1)
            val group2 = InstallationGroup(
                "red",
                "red"
            )
            installationGroupRepo.save(group2)
            val group3 = InstallationGroup(
                "blue",
                "blue"
            )
            installationGroupRepo.save(group3)
        }
    }

    fun createTestInstallations() {
        if (installationRepo.count() == 0.toLong()) {

            val groups = installationGroupRepo.findAll().toList()

            val inst1 = Installation(
                "https",
                "nsd1.exeki.local"
            )
            inst1.important = true
            inst1.groups.addAll(groups)
            val inst2 = Installation(
                "https",
                "nsd2.exeki.local"
            )
            inst2.groups.addAll(groups.take(groups.size - 1))
            val inst3 = Installation(
                "https",
                "nsd3.exeki.local"
            )
            inst3.groups.addAll(groups.take(groups.size - 2))
            installationRepo.save(inst1)
            installationRepo.save(inst2)
            installationRepo.save(inst3)
        }
    }
}