package ru.kazantsev.nsd.configMigrator.ui

import com.vaadin.flow.component.applayout.AppLayout
import com.vaadin.flow.component.applayout.DrawerToggle
import com.vaadin.flow.component.html.H1
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.theme.lumo.LumoUtility
import ru.kazantsev.nsd.configMigrator.ui.views.MainView


class MainLayout : AppLayout() {

    init {
        createHeader()
        createDrawer()
    }

    private fun createHeader() {
        val logo = H1("NSD Config Control")

        logo.addClassNames(
            LumoUtility.FontSize.LARGE,
            LumoUtility.Margin.MEDIUM
        )

        val header = HorizontalLayout(DrawerToggle(), logo).apply {
            defaultVerticalComponentAlignment = FlexComponent.Alignment.CENTER
            setWidthFull()
            addClassNames(
                LumoUtility.Padding.Vertical.NONE,
                LumoUtility.Padding.Horizontal.MEDIUM
            )
        }

        addToNavbar(header)
    }

    private fun createDrawer() {
        addToDrawer(
            VerticalLayout(
                RouterLink("Список инсталляций", MainView::class.java),
                RouterLink("Миграции в процессе TODO", MainView::class.java),
                RouterLink("Ключевые бекапы TODO", MainView::class.java),
            )
        )
    }
}