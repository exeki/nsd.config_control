package ru.kazantsev.nsd.configMigrator.view.components

import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import ru.kazantsev.nsd.configMigrator.data.model.Installation
import ru.kazantsev.nsd.configMigrator.data.repo.InstallationRepo

@Component
class InstallationView @Autowired constructor(private val installationRepo: InstallationRepo) : VerticalLayout() {



    init {

    }
}
