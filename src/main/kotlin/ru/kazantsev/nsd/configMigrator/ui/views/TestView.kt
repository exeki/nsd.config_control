package ru.kazantsev.nsd.configMigrator.ui.views

import com.vaadin.flow.component.ClickEvent
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.details.Details
import com.vaadin.flow.component.html.H3
import com.vaadin.flow.component.html.Span
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.treegrid.TreeGrid
import com.vaadin.flow.router.Route
import com.vaadin.flow.spring.annotation.UIScope
import com.vaadin.flow.spring.annotation.VaadinSessionScope
import jakarta.annotation.security.PermitAll
import ru.kazantsev.nsd.configMigrator.data.model.Installation
import ru.kazantsev.nsd.configMigrator.ui.MainLayout


@UIScope
@VaadinSessionScope
@Route(value = "test", layout = MainLayout::class)
@PermitAll
class TestView : VerticalLayout() {

    private fun createButtonVariant(variant: ButtonVariant): Button {
        return Button(variant.toString()).apply {
            addThemeVariants(variant)
        }
    }

    val buttons = HorizontalLayout()

    val tree = TreeGrid<Installation>()

    init {

        val details = Details(H3("СУПЕРБОЛШОЙ"), Span("Содержимое компонента"))

        // Кнопка для управления состоянием Details
        val toggleButton = Button(
            "Развернуть/Свернуть"
        ) { event: ClickEvent<Button?>? ->
            details.isOpened = !details.isOpened // Переключаем состояние
        }

        // Добавляем компоненты на layout
        add(details, toggleButton)

        ButtonVariant.entries.forEach { buttons.add(createButtonVariant(it)) }

        add(
            H3("Кнопочки:"),
            buttons
        )
    }
}