package ru.kazantsev.nsd.configMigrator.ui.views

import com.vaadin.flow.component.UI
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.checkbox.Checkbox
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.data.provider.ListDataProvider
import com.vaadin.flow.router.Route
import com.vaadin.flow.spring.annotation.UIScope
import com.vaadin.flow.spring.annotation.VaadinSessionScope
import jakarta.annotation.security.PermitAll
import ru.kazantsev.nsd.configMigrator.data.model.User
import ru.kazantsev.nsd.configMigrator.data.repo.UserAuthorityRepo
import ru.kazantsev.nsd.configMigrator.data.repo.UserRepo
import ru.kazantsev.nsd.configMigrator.ui.MainLayout
import ru.kazantsev.nsd.configMigrator.ui.views.`object`.UserView
import ru.kazantsev.nsd.configMigrator.ui.utils.DateTimeFormatUtils.Companion.format

@Route("users", layout = MainLayout::class)
@UIScope
@VaadinSessionScope
//TODO не работает, нужно разобраться
//@RolesAllowed("admin")
@PermitAll
class UsersListView(
    private val userRepo: UserRepo,
    private val userAuthorityRepo: UserAuthorityRepo
) : VerticalLayout() {

    private val usersDataProvider = ListDataProvider(userRepo.findByArchivedIs(false))

    init {
        add(
            HorizontalLayout().apply {
                defaultVerticalComponentAlignment = FlexComponent.Alignment.CENTER
                add(
                    Button("Добавить пользователя").apply {
                        //TODO добавление пользователя
                    },
                    Checkbox("Показать архивные").apply {
                        addValueChangeListener { event ->
                            usersDataProvider.items.clear()
                            usersDataProvider.items.addAll(userRepo.findByArchivedIs(event.value))
                            usersDataProvider.refreshAll()
                        }
                    }
                )
            },

            Grid<User>().apply {
                isAllRowsVisible = true
                setSizeFull()
                removeAllColumns()
                addColumn(User::id).setHeader("ID")
                addColumn(User::username).setHeader("Логин")
                addColumn(User::fullName).setHeader("Имя")
                addColumn { user -> user.authorities.joinToString { it.title } }.setHeader("Права")
                addColumn { user -> format(user.createdDate) }.setHeader("Дата создания")
                addColumn { user -> format(user.lastModifiedDate) }.setHeader("Дата изменения")
                dataProvider = usersDataProvider
                addItemClickListener { event ->
                    UI.getCurrent().navigate(UserView::class.java, event.item.id)
                }
            }
        )
    }
}