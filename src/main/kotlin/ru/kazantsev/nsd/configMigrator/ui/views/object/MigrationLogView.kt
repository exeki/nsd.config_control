package ru.kazantsev.nsd.configMigrator.ui.views.`object`

import com.vaadin.flow.component.UI
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.BeforeEvent
import com.vaadin.flow.router.HasUrlParameter
import com.vaadin.flow.router.Route
import com.vaadin.flow.spring.annotation.UIScope
import com.vaadin.flow.spring.annotation.VaadinSessionScope
import jakarta.annotation.security.PermitAll
import ru.kazantsev.nsd.configMigrator.data.model.MigrationLog
import ru.kazantsev.nsd.configMigrator.data.repo.MigrationLogRepo
import ru.kazantsev.nsd.configMigrator.ui.MainLayout
import ru.kazantsev.nsd.configMigrator.ui.views.error.Error400
import ru.kazantsev.nsd.configMigrator.ui.views.error.Error404

@UIScope
@VaadinSessionScope
@Route(layout = MainLayout::class)
@PermitAll
class MigrationLogView (
    private val migrationLogRepo: MigrationLogRepo
) : VerticalLayout(), HasUrlParameter<Long> {

    private lateinit var migrationLog : MigrationLog

    override fun setParameter(event: BeforeEvent?, parameter: Long?) {
        if (parameter == null) UI.getCurrent().navigate(Error400::class.java)
        else migrationLogRepo.findById(parameter).ifPresentOrElse(
            { value -> renderObjectCard(value) },
            { UI.getCurrent().navigate(Error404::class.java) }
        )
    }

    private fun renderObjectCard(obj : MigrationLog) {
        migrationLog = obj
        this.removeAll()
        //TODO
    }
}