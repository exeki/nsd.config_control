package ru.kazantsev.nsd.configMigrator.ui.views.error

import com.vaadin.flow.component.UI
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.html.H2
import com.vaadin.flow.component.html.H3
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.Route
import com.vaadin.flow.spring.annotation.UIScope
import com.vaadin.flow.spring.annotation.VaadinSessionScope
import jakarta.annotation.security.PermitAll
import ru.kazantsev.nsd.configMigrator.ui.views.InstallationListView
import ru.kazantsev.nsd.configMigrator.ui.MainLayout

@UIScope
@VaadinSessionScope
@Route(value = "403", layout = MainLayout::class)
@PermitAll
class Error403 : VerticalLayout(){
    init {
        setSizeFull()
        alignItems = FlexComponent.Alignment.CENTER
        add(
            H2("403 Forbidden"),
            H3("Ты просишь без уважения"),
            Button("На главную") { _ -> UI.getCurrent().navigate(InstallationListView::class.java) }
        )
    }
}