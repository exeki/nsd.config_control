package ru.kazantsev.nsd.configMigrator.view.error

import com.vaadin.flow.component.UI
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.html.H2
import com.vaadin.flow.component.html.H3
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.Route
import ru.kazantsev.nsd.configMigrator.view.MainView
import ru.kazantsev.nsd.configMigrator.view.MainLayout

@Route(value = "404", layout = MainLayout::class)
class Error404 : VerticalLayout() {
    init {
        setSizeFull()
        alignItems = FlexComponent.Alignment.CENTER

        val title = H2("404 Not Found")
        add(title)

        val description = H3("The requested page could not be found.")
        add(description)

        val backButton = Button("Back to Home")
        backButton.addClickListener { _ -> UI.getCurrent().navigate(MainView::class.java) }
        add(backButton)
    }
}