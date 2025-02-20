package ru.kazantsev.nsd.configMigrator.ui.views

import com.vaadin.flow.component.UI
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.checkbox.Checkbox
import com.vaadin.flow.component.combobox.MultiSelectComboBox
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.formlayout.FormLayout
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.html.H3
import com.vaadin.flow.component.notification.Notification
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.textfield.PasswordField
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.binder.Binder
import com.vaadin.flow.data.provider.ListDataProvider
import com.vaadin.flow.router.Route
import com.vaadin.flow.spring.annotation.UIScope
import com.vaadin.flow.spring.annotation.VaadinSessionScope
import jakarta.annotation.security.PermitAll
import org.springframework.security.crypto.password.PasswordEncoder
import ru.kazantsev.nsd.configMigrator.data.model.User
import ru.kazantsev.nsd.configMigrator.data.model.UserAuthority
import ru.kazantsev.nsd.configMigrator.data.repo.UserAuthorityRepo
import ru.kazantsev.nsd.configMigrator.data.repo.UserRepo
import ru.kazantsev.nsd.configMigrator.ui.MainLayout
import ru.kazantsev.nsd.configMigrator.ui.components.FormErrorNotification
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
    private val userAuthorityRepo: UserAuthorityRepo,
    private val passwordEncoder: PasswordEncoder,
) : VerticalLayout() {

    private val usersDataProvider = ListDataProvider(userRepo.findByArchivedIs(false))

    init {
        add(
            HorizontalLayout().apply {
                defaultVerticalComponentAlignment = FlexComponent.Alignment.CENTER
                add(
                    Button("Добавить пользователя").apply {
                        addClickListener {
                            val dialog = Dialog()
                            val binder = Binder(User::class.java)
                            val errorContainer = FormErrorNotification()
                            dialog.apply {
                                add(
                                    FormLayout(
                                        VerticalLayout(
                                            H3("Добавить пользователя"),
                                            errorContainer,
                                            TextField("Фамилия").apply {
                                                setSizeFull()
                                                isRequiredIndicatorVisible = true
                                                binder.forField(this).asRequired().bind(User::lastName.name)
                                            },
                                            TextField("Имя").apply {
                                                setSizeFull()
                                                isRequiredIndicatorVisible = true
                                                binder.forField(this).asRequired().bind(User::firstName.name)
                                            },
                                            TextField("Отчество").apply {
                                                setSizeFull()
                                                binder.forField(this).bind(User::middleName.name)
                                            },
                                            TextField("Логин").apply {
                                                setSizeFull()
                                                isRequiredIndicatorVisible = true
                                                binder.forField(this).asRequired().bind(User::username.name)
                                            },
                                            PasswordField("Пароль").apply {
                                                setSizeFull()
                                                isEnabled = true
                                                isRequiredIndicatorVisible = true
                                                binder.forField(this).asRequired().bind(User::password.name)
                                            },
                                            MultiSelectComboBox<UserAuthority>("Группы пользователей").apply {
                                                setSizeFull()
                                                isRequiredIndicatorVisible = true
                                                setItems(userAuthorityRepo.findAll().toList())
                                                setItemLabelGenerator(UserAuthority::title)
                                                binder.forField(this).bind(User::authorities.name)
                                            },
                                            HorizontalLayout(
                                                Button("Сохранить").apply {
                                                    addClickListener {
                                                        val user = User()
                                                        if (!binder.writeBeanIfValid(user)) Notification.show("Пожалуйста, заполните все обязательные поля!")
                                                        else {
                                                            try {
                                                                user.password = passwordEncoder.encode(user.password)
                                                                userRepo.save(user)
                                                                usersDataProvider.items.add(user)
                                                                usersDataProvider.refreshAll()
                                                                Notification.show("Пользователь ${user.fullName} добавлен.")
                                                                dialog.close()
                                                            } catch (e: Exception) {
                                                                errorContainer.show("Не удалось сохранить группу: " + e.message)
                                                            }
                                                        }
                                                    }
                                                },
                                                Button("Отменить") { dialog.close() }
                                            )
                                        )
                                    ).apply { style.setMinWidth("500px") }
                                )
                            }
                            dialog.open()
                        }
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