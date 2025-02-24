package ru.kazantsev.nsd.configMigrator.ui

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.applayout.AppLayout
import com.vaadin.flow.component.applayout.DrawerToggle
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.html.H1
import com.vaadin.flow.component.html.Span
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.dom.Style
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.theme.lumo.LumoUtility
import ru.kazantsev.nsd.configMigrator.services.SecurityService
import ru.kazantsev.nsd.configMigrator.ui.components.BoldSpan
import ru.kazantsev.nsd.configMigrator.ui.views.*
import ru.kazantsev.nsd.configMigrator.ui.views.`object`.UserView

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
                LumoUtility.Padding.Horizontal.SMALL
            )
            add(
                HorizontalLayout().apply {
                    style.setPosition(Style.Position.ABSOLUTE)
                    style.setRight("10px")
                    this.justifyContentMode = FlexComponent.JustifyContentMode.END
                    val user = securityService.authenticatedUser
                    if (user != null) {
                        val link = if (user.id != null) RouterLink(user.fullName, UserView::class.java, user.id)
                        else BoldSpan(user.fullName)
                        this.add(link)
                        setAlignSelf(FlexComponent.Alignment.CENTER, link)
                    }
                    val logout = Button("Выйти") { securityService.logout() }
                    this.add(logout)
                    setAlignSelf(FlexComponent.Alignment.CENTER, logout)
                }
            )

        }

        addToNavbar(header)
    }

    private fun createDrawer() {
        addToDrawer(
            VerticalLayout(
                RouterLink("Главная", MainView::class.java),
                RouterLink("Список инсталляций", InstallationListView::class.java),
                RouterLink("Пути миграций", MigrationPathList::class.java),
                RouterLink("Планировщики TODO", SchedulerListView::class.java), //TODO Планировщики
                RouterLink("Аудит лог TODO", MainView::class.java), //TODO Аудит лог
                RouterLink("Релизы и скрипты TODO", MainView::class.java), //TODO Релизы и скрипты
                RouterLink("Пользователи", UsersListView::class.java),
                RouterLink("ТЕСТ", TestView::class.java),
            )
        )
    }
}