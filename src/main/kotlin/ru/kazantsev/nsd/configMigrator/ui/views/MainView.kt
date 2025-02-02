package ru.kazantsev.nsd.configMigrator.ui.views

import com.vaadin.flow.component.UI
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.Route
import com.vaadin.flow.spring.annotation.UIScope
import com.vaadin.flow.spring.annotation.VaadinSessionScope
import jakarta.annotation.security.PermitAll
import ru.kazantsev.nsd.configMigrator.data.repo.InstallationRepo
import ru.kazantsev.nsd.configMigrator.ui.MainLayout
import ru.kazantsev.nsd.configMigrator.ui.components.Card

@UIScope
@VaadinSessionScope
@Route(layout = MainLayout::class)
@PermitAll
class MainView(private val installationRepo: InstallationRepo) : VerticalLayout() {

    init {
        val card1Title: String = installationRepo.countByArchivedIs(false).toString() + " инсталляций на стенде"
        add(
            HorizontalLayout(
                Card(card1Title, " ").apply {
                    addClickListener { UI.getCurrent().navigate(InstallationListView::class.java) }
                },
                Card(card1Title, " asd").apply {
                    addClickListener { UI.getCurrent().navigate(InstallationListView::class.java) }
                },
                Card(card1Title, " ").apply {
                    addClickListener { UI.getCurrent().navigate(InstallationListView::class.java) }
                }
            )
        )
    }
}