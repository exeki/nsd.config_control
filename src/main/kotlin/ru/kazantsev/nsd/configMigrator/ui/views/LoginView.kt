package ru.kazantsev.nsd.configMigrator.ui.views

import com.vaadin.flow.component.login.LoginForm
import com.vaadin.flow.component.login.LoginI18n
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.BeforeEnterEvent
import com.vaadin.flow.router.BeforeEnterObserver
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import com.vaadin.flow.server.auth.AnonymousAllowed
import org.slf4j.LoggerFactory
import ru.kazantsev.nsd.configMigrator.services.SecurityService

@Route("login")
@PageTitle("Login | NSD CC")
@AnonymousAllowed
class LoginView : VerticalLayout(), BeforeEnterObserver {

    private val logger = LoggerFactory.getLogger(LoginView::class.java)

    private val loginForm = LoginForm().apply {
        alignItems = FlexComponent.Alignment.CENTER
        action = "login"
        setI18n(
            LoginI18n.createDefault().apply {
                form.apply {
                    title = "Welcome to the club, buddy"
                    username = "Имя пользователя"
                    password = "Пароль"
                    submit = "Войти?"
                    isForgotPasswordButtonVisible = false
                }
                errorMessage.apply {
                    title = "Не удалось авторизоваться"
                    message = null
                }
            }
        )
    }

    init {
        add(loginForm)
    }

    override fun beforeEnter(beforeEnterEvent: BeforeEnterEvent) {
        if (beforeEnterEvent.location.queryParameters.parameters.containsKey("error")) {
            loginForm.isError = true
        }
    }

}