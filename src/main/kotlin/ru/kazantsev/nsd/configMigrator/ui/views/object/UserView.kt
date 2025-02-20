package ru.kazantsev.nsd.configMigrator.ui.views.`object`

import com.vaadin.flow.component.UI
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.combobox.ComboBox
import com.vaadin.flow.component.combobox.MultiSelectComboBox
import com.vaadin.flow.component.details.Details
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.formlayout.FormLayout
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.html.H2
import com.vaadin.flow.component.html.H3
import com.vaadin.flow.component.html.Span
import com.vaadin.flow.component.notification.Notification
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.textfield.IntegerField
import com.vaadin.flow.component.textfield.PasswordField
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.binder.Binder
import com.vaadin.flow.data.provider.ListDataProvider
import com.vaadin.flow.router.BeforeEvent
import com.vaadin.flow.router.HasUrlParameter
import com.vaadin.flow.router.Route
import com.vaadin.flow.spring.annotation.UIScope
import com.vaadin.flow.spring.annotation.VaadinSessionScope
import jakarta.annotation.security.PermitAll
import org.springframework.security.crypto.password.PasswordEncoder
import ru.kazantsev.nsd.configMigrator.data.model.AccessKey
import ru.kazantsev.nsd.configMigrator.data.model.Installation
import ru.kazantsev.nsd.configMigrator.data.model.User
import ru.kazantsev.nsd.configMigrator.data.model.UserAuthority
import ru.kazantsev.nsd.configMigrator.data.repo.AccessKeyRepo
import ru.kazantsev.nsd.configMigrator.data.repo.InstallationRepo
import ru.kazantsev.nsd.configMigrator.data.repo.UserAuthorityRepo
import ru.kazantsev.nsd.configMigrator.data.repo.UserRepo
import ru.kazantsev.nsd.configMigrator.services.ConnectorService
import ru.kazantsev.nsd.configMigrator.services.InstallationService
import ru.kazantsev.nsd.configMigrator.services.SecurityService
import ru.kazantsev.nsd.configMigrator.ui.MainLayout
import ru.kazantsev.nsd.configMigrator.ui.components.FormErrorNotification
import ru.kazantsev.nsd.configMigrator.ui.components.PropertyField
import ru.kazantsev.nsd.configMigrator.ui.views.error.Error400
import ru.kazantsev.nsd.configMigrator.ui.views.error.Error404
import ru.kazantsev.nsd.configMigrator.ui.utils.DateTimeFormatUtils.Companion.format
import ru.kazantsev.nsd.configMigrator.ui.views.UsersListView
import java.time.LocalDateTime

@UIScope
@VaadinSessionScope
@Route(value = "user", layout = MainLayout::class)
@PermitAll
class UserView(
    private val userRepo: UserRepo,
    private val accessKeyRepo: AccessKeyRepo,
    private val installationRepo: InstallationRepo,
    private val securityService: SecurityService,
    private val userAuthorityRepo: UserAuthorityRepo,
    private val passwordEncoder: PasswordEncoder,
    private val connectorService: ConnectorService
) : VerticalLayout(), HasUrlParameter<Long> {

    private lateinit var user: User

    private lateinit var accessKeyDataProvider: ListDataProvider<AccessKey>

    override fun setParameter(event: BeforeEvent?, id: Long?) {
        if (id == null) UI.getCurrent().navigate(Error400::class.java)
        else userRepo.findById(id).ifPresentOrElse(
            { value -> renderObjectCard(value) },
            { UI.getCurrent().navigate(Error404::class.java) }
        )
    }

    private fun renderObjectCard(obj: User) {
        this.removeAll()
        this.user = obj
        this.accessKeyDataProvider = ListDataProvider(accessKeyRepo.findByUser(user))

        add(
            H2("${if (user.archived) "АРХИВ | " else ""}Пользователь \"${user.fullName}\""),
            HorizontalLayout().apply {
                if (securityService.isUser(user) || securityService.isAdmin) add(
                    Button("Сменить пароль").apply {
                        addClickListener {
                            val dialog = Dialog()
                            val errorContainer = FormErrorNotification()
                            val oldPassField = PasswordField("Старый пароль").apply {
                                setSizeFull()
                                isRequiredIndicatorVisible = true
                            }
                            val newPassField = PasswordField("Новый пароль").apply {
                                setSizeFull()
                                isRequiredIndicatorVisible = true
                            }
                            dialog.apply {
                                add(
                                    FormLayout(
                                        VerticalLayout(
                                            H3("Сменить пароль"),
                                            errorContainer,
                                            if (!securityService.isAdmin) oldPassField else Span(),
                                            newPassField,
                                            HorizontalLayout(
                                                Button("Сохранить").apply {
                                                    addClickListener {
                                                        if ((!securityService.isAdmin && oldPassField.value == null) || newPassField.value == null) {
                                                            errorContainer.show("Пожалуйста, заполните пароли")
                                                        } else {
                                                            val oldPasswordIsOk = passwordEncoder.matches(
                                                                oldPassField.value,
                                                                user.password
                                                            ) || securityService.isAdmin
                                                            if (!oldPasswordIsOk) errorContainer.show("Старый пароль не подходит")
                                                            else {
                                                                try {
                                                                    user.password =
                                                                        passwordEncoder.encode(newPassField.value)
                                                                    userRepo.save(user)
                                                                    dialog.close()
                                                                    Notification.show("Пароль изменен.")
                                                                } catch (e: Exception) {
                                                                    errorContainer.show("Не удалось сменить пароль: " + e.message)
                                                                }
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

                    }
                )
                if (securityService.isAdmin) add(
                    Button("Редактировать").apply {
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
                                                value = user.lastName
                                                isRequiredIndicatorVisible = true
                                                binder.forField(this).asRequired().bind(User::lastName.name)
                                            },
                                            TextField("Имя").apply {
                                                setSizeFull()
                                                value = user.firstName
                                                isRequiredIndicatorVisible = true
                                                binder.forField(this).asRequired().bind(User::firstName.name)
                                            },
                                            TextField("Отчество").apply {
                                                setSizeFull()
                                                value = user.middleName
                                                binder.forField(this).bind(User::middleName.name)
                                            },
                                            TextField("Логин").apply {
                                                setSizeFull()
                                                value = user.username
                                                isRequiredIndicatorVisible = true
                                                binder.forField(this).asRequired().bind(User::username.name)
                                            },
                                            MultiSelectComboBox<UserAuthority>("Группы пользователей").apply {
                                                setSizeFull()
                                                isRequiredIndicatorVisible = true
                                                setItems(userAuthorityRepo.findAll().toList())
                                                value = user.authorities
                                                setItemLabelGenerator(UserAuthority::title)
                                                binder.forField(this).bind(User::authorities.name)
                                            },
                                            HorizontalLayout(
                                                Button("Сохранить").apply {
                                                    addClickListener {
                                                        if (!binder.writeBeanIfValid(user)) errorContainer.show("Пожалуйста, заполните все обязательные поля!")
                                                        else {
                                                            try {
                                                                userRepo.save(user)
                                                                dialog.close()
                                                                UI.getCurrent().page.reload()
                                                                Notification.show("Пользователь ${user.fullName} изменен.")
                                                            } catch (e: Exception) {
                                                                errorContainer.show("Не удалось сохранить пользователя: " + e.message)
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
                    if (user.archived) Button("Восстановить из архива").apply {
                        addClickListener {
                            user.archived = !user.archived
                            userRepo.save(user)
                            UI.getCurrent().page.reload()
                        }
                    }
                    else Button("Архивировать").apply {
                        addClickListener {
                            user.archived = !user.archived
                            userRepo.save(user)
                            UI.getCurrent().page.reload()
                        }
                    },
                    Button("Удалить").apply {
                        addThemeVariants(ButtonVariant.LUMO_ERROR)
                        addClickListener {
                            val dialog = Dialog()
                            val errorContainer = FormErrorNotification()
                            dialog.add(
                                FormLayout(
                                    VerticalLayout(
                                        H3("Удалить пользователя"),
                                        errorContainer,
                                        Div().apply { text = "Вы уверены?" },
                                        HorizontalLayout(
                                            Button("Удалить").apply {
                                                addThemeVariants(ButtonVariant.LUMO_ERROR)
                                                addClickListener {
                                                    try {
                                                        if (securityService.authenticatedUser!!.username == user.username) {
                                                            errorContainer.show("Сомовыпил не выход. Нельзя удалить себя.")
                                                        } else {
                                                            accessKeyRepo.findByUser(user).forEach {
                                                                accessKeyRepo.delete(it)
                                                            }
                                                            userRepo.delete(user)
                                                            UI.getCurrent().navigate(UsersListView::class.java)
                                                            dialog.close()
                                                        }
                                                    } catch (e: Exception) {
                                                        errorContainer.show(
                                                            "Не удалось удалить пользовтеля: ${e.message}"
                                                        )
                                                    }

                                                }
                                            },
                                            Button("Отмена") { dialog.close() }
                                        )
                                    )
                                ).apply { style.setWidth("500px") }
                            )
                            dialog.open()
                        }

                    },
                )
            },
            HorizontalLayout().apply {
                setSizeFull()
                add(
                    Details(H3("Параметры")).apply {
                        style.setWidth("40%")
                        isOpened = true
                        add(
                            PropertyField("ID", user.id),
                            PropertyField("Логин", user.username),
                            PropertyField("Имя", user.firstName),
                            PropertyField("Фамилия", user.lastName),
                            PropertyField("Отчество", user.middleName),
                            PropertyField("Группы пользователей", user.authorities.joinToString(", ") { it.title })
                        )
                    },
                    Details(H3("Ключи")).apply {
                        style.setWidth("60%")
                        isOpened = true

                        if (securityService.isUser(user) || securityService.isAdmin) add(
                            Button("Добавить").apply {
                                addClickListener {
                                    val dialog = Dialog()
                                    val errorContainer = FormErrorNotification()
                                    val installationSelect = ComboBox<Installation>("Инсталляция").apply {
                                        setSizeFull()
                                        setItems(installationRepo.findByArchivedIs(false).toList())
                                        setItemLabelGenerator(Installation::host)
                                    }
                                    val loginInput = TextField("Логин").apply {
                                        setSizeFull()
                                        isRequiredIndicatorVisible = true
                                    }
                                    val passwordInput = PasswordField("Пароль").apply {
                                        setSizeFull()
                                        isRequiredIndicatorVisible = true
                                    }
                                    val intInput = IntegerField("Срок годности в минутах").apply {
                                        setSizeFull()
                                        value = 1000
                                        isRequiredIndicatorVisible = true
                                    }
                                    dialog.add(
                                        FormLayout(
                                            VerticalLayout(
                                                H3("Добавить ключ"),
                                                errorContainer,
                                                installationSelect,
                                                loginInput,
                                                passwordInput,
                                                intInput,
                                                HorizontalLayout(
                                                    Button("Сохранить").apply {
                                                        addThemeVariants(ButtonVariant.LUMO_PRIMARY)
                                                        addClickListener {
                                                            if (listOf(
                                                                    intInput,
                                                                    installationSelect,
                                                                    passwordInput,
                                                                    loginInput
                                                                ).any { it.isEmpty() }
                                                            ) errorContainer.show("Пожалуйста, заполните все обязательные поля!")
                                                            else {
                                                                try {
                                                                    val con =
                                                                        connectorService.getConnectorForInstallation(
                                                                            installationSelect.value
                                                                        )
                                                                    val key = con.getAccessKey(
                                                                        loginInput.value,
                                                                        passwordInput.value,
                                                                        intInput.value
                                                                    )
                                                                    val accessKey = AccessKey(
                                                                        key,
                                                                        user,
                                                                        installationSelect.value,
                                                                        LocalDateTime.now()
                                                                            .plusMinutes(intInput.value!!.toLong())
                                                                    )
                                                                    accessKeyRepo.save(accessKey)
                                                                    accessKeyDataProvider.items.add(accessKey)
                                                                    accessKeyDataProvider.refreshAll()
                                                                    dialog.close()
                                                                    Notification.show("Ключ успешно добавлен.")
                                                                } catch (e: Exception) {
                                                                    errorContainer.show(
                                                                        "Не удалось получить ключ: ${e.message}"
                                                                    )
                                                                }
                                                            }
                                                        }
                                                    },
                                                    Button("Отмена") { dialog.close() }
                                                )
                                            )
                                        ).apply { style.setWidth("500px") }
                                    )
                                    dialog.open()
                                }
                            }
                        )

                        add(
                            Grid(AccessKey::class.java).apply {
                                setSizeFull()
                                removeAllColumns()
                                addColumn(AccessKey::id).setHeader("ID")
                                addColumn { it.installation.host }.setHeader("Инсталляция")
                                addColumn { format(it.date) }.setHeader("Годен до")
                                isAllRowsVisible = true
                                dataProvider = accessKeyDataProvider
                                if (securityService.isUser(user) || securityService.isAdmin) addComponentColumn { item ->
                                    HorizontalLayout(
                                        Button("Удалить").apply {
                                            addThemeVariants(ButtonVariant.LUMO_ERROR)
                                            addClickListener {
                                                try {
                                                    accessKeyRepo.delete(item)
                                                    accessKeyDataProvider.items.remove(item)
                                                    accessKeyDataProvider.refreshAll()
                                                    //TODO инвалидация ключа путем отправки скрипта на инсталляцию (если он еще активен)
                                                } catch (e: Exception) {
                                                    Notification.show("Не удалось удалить ключ: ${e.message}")
                                                }
                                            }
                                        }
                                    )
                                }.setHeader("Действия")
                            }
                        )
                    }
                )
            }
            //TODO список доступных инсталляций
        )
    }
}