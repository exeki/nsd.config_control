package ru.kazantsev.nsd.configMigrator.ui

import com.vaadin.flow.component.applayout.AppLayout
import com.vaadin.flow.component.applayout.DrawerToggle
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.html.H1
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.theme.lumo.LumoUtility
import ru.kazantsev.nsd.configMigrator.services.SecurityService
import ru.kazantsev.nsd.configMigrator.ui.views.InstallationListView
import ru.kazantsev.nsd.configMigrator.ui.views.MainView
import ru.kazantsev.nsd.configMigrator.ui.views.SchedulerListView
import ru.kazantsev.nsd.configMigrator.ui.views.TestView

class MainLayout(
    private val securityService: SecurityService
) : AppLayout() {

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
            if (securityService.authenticatedUser != null) {
                val logout = Button("Выйти") { securityService.logout() }
                add(logout)
            }
        }

        addToNavbar(header)
    }

    private fun createDrawer() {
        addToDrawer(
            VerticalLayout(
                RouterLink("Главная", MainView::class.java),
                RouterLink("Список инсталляций", InstallationListView::class.java),
                RouterLink("Пути миграций TODO", MainView::class.java),
                RouterLink("Миграции в процессе TODO а надо ли?", MainView::class.java),
                RouterLink("Ключевые бекапы TODO", MainView::class.java),
                RouterLink("Планировщики TODO", SchedulerListView::class.java),
                RouterLink("Аудит лог TODO", MainView::class.java),
                RouterLink("Релизы и скрипты TODO", MainView::class.java),
                RouterLink("ТЕСТ", TestView::class.java),
            )
        )
    }
}