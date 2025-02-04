package ru.kazantsev.nsd.configMigrator.ui.views.`object`

import com.vaadin.flow.component.UI
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.details.Details
import com.vaadin.flow.component.html.H2
import com.vaadin.flow.component.html.H3
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.BeforeEvent
import com.vaadin.flow.router.HasUrlParameter
import com.vaadin.flow.router.Route
import com.vaadin.flow.spring.annotation.UIScope
import com.vaadin.flow.spring.annotation.VaadinSessionScope
import jakarta.annotation.security.PermitAll
import ru.kazantsev.nsd.configMigrator.data.model.User
import ru.kazantsev.nsd.configMigrator.data.repo.UserRepo
import ru.kazantsev.nsd.configMigrator.ui.MainLayout
import ru.kazantsev.nsd.configMigrator.ui.components.PropertyField
import ru.kazantsev.nsd.configMigrator.ui.views.error.Error400
import ru.kazantsev.nsd.configMigrator.ui.views.error.Error404

@UIScope
@VaadinSessionScope
@Route(value = "user", layout = MainLayout::class)
@PermitAll
class UserView(
    private val userRepo: UserRepo
) : VerticalLayout(), HasUrlParameter<Long> {

    private lateinit var user: User

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
        add(
            H2("${if (user.archived) "АРХИВ | " else ""}Пользователь \"${user.fullName}\""),
            HorizontalLayout().apply {
                add(
                    Button("Редактировать").apply {
                        addClickListener {
                            //TODO
                        }
                    },
                    Button("Сменить пароль").apply {
                        addClickListener {
                            //TODO
                        }
                    },
                    if (user.archived) Button("Восстановить из архива").apply {
                        addClickListener {
                            //TODO
                        }
                    }
                    else Button("Архивировать").apply {
                        addClickListener {
                            //TODO
                        }
                    },
                    Button("Удалить").apply {
                        addThemeVariants(ButtonVariant.LUMO_ERROR)
                        addClickListener {
                            //TODO
                        }
                    },
                )
            },
            Details(H3("Параметры")).apply {
                style.setWidth("40%")
                isOpened = true
                add(
                    PropertyField("ID", user.id),
                    PropertyField("Логин", user.username),
                    PropertyField("Имя", user.firstName),
                    PropertyField("Фамилия", user.lastName),
                    PropertyField("Отчество", user.middleName),
                )
            }
            //TODO список доступных инсталляций
            //TODO ключи доступа пользователя
        )
    }
}