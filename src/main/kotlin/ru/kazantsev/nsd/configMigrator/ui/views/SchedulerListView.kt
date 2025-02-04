package ru.kazantsev.nsd.configMigrator.ui.views

import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.Route
import com.vaadin.flow.spring.annotation.UIScope
import com.vaadin.flow.spring.annotation.VaadinSessionScope
import jakarta.annotation.security.PermitAll
import ru.kazantsev.nsd.configMigrator.data.repo.SchedulerRepo
import ru.kazantsev.nsd.configMigrator.ui.MainLayout

@UIScope
@VaadinSessionScope
@Route(value = "schedulers", layout = MainLayout::class)
@PermitAll
class SchedulerListView(
    private val schedulerRepo: SchedulerRepo
) : VerticalLayout() {
    init {
        //TODO
    }
}