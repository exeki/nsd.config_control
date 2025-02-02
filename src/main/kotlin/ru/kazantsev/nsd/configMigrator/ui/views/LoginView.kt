
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
import org.springframework.security.provisioning.UserDetailsManager

@Route("login")
@PageTitle("Login | NSD CC")
@AnonymousAllowed

class LoginView (
    private val userDetailsManager: UserDetailsManager,
) : VerticalLayout(), BeforeEnterObserver {

    private val logger = LoggerFactory.getLogger(LoginView::class.java)

    init {
        logger.warn("userDetailsManager class: " + userDetailsManager::class.java.name)
        if(userDetailsManager.userExists("admin")){
            logger.warn("admin СУЩЕСТВУЕТ, пароль: " +
            userDetailsManager.loadUserByUsername("admin")!!.password)
        }
        if(userDetailsManager.userExists("user")){
            logger.warn("user СУЩЕСТВУЕТ, пароль: " +
                    userDetailsManager.loadUserByUsername("user")!!.password)
        }
        userDetailsManager
    }

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
                    forgotPassword = "Забыл пароль"
                }
                errorMessage.apply {
                    title = "Ошибка"
                    message = "Что то пощло не так, но что я тебе не скажу"
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
        } //else UI.getCurrent().navigate(MainView::class.java)
    }

}