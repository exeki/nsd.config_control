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
                "nsd1.exeki.local",
                "4deb6225-c637-44d7-8f21-27fa2e55d494"
            )
            inst1.important = true
            inst1.groups.addAll(groups)
            val inst2 = Installation(
                "https",
                "nsd2.exeki.local",
                "48565970-6d64-438b-a025-bad3275c50e5"
            )
            inst2.groups.addAll(groups.take(groups.size - 1))
            val inst3 = Installation(
                "https",
                "nsd3.exeki.local",
                "a3ae3f8c-33f7-43bd-bcbc-44fefaa39e35"
            )
            inst3.groups.addAll(groups.take(groups.size - 2))
            installationService.updateInstallation(inst1)
            installationService.updateInstallation(inst2)
            installationService.updateInstallation(inst3)
        }
    }
}