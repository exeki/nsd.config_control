package ru.kazantsev.sportiksmonitor.view.error

import com.vaadin.flow.component.UI
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.html.H2
import com.vaadin.flow.component.html.H3
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.Route
import ru.kazantsev.sportiksmonitor.view.MainView
import ru.kazantsev.sportiksmonitor.view.MainLayout

@Route(value = "400", layout = MainLayout::class)
class Error400 : VerticalLayout() {
    init {
        setSizeFull()
        alignItems = FlexComponent.Alignment.CENTER

        val title = H2("400 Bad Request")
        add(title)

        val description = H3("Чет хео ты обратился.")
        add(description)

        val backButton = Button("Back to Home")
        backButton.addClickListener { _ -> UI.getCurrent().navigate(MainView::class.java) }
        add(backButton)
    }
}