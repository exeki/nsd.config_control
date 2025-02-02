package ru.kazantsev.nsd.configMigrator.services

import com.vaadin.flow.component.UI
import com.vaadin.flow.server.VaadinServletRequest
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler
import org.springframework.stereotype.Component

@Component
class SecurityService {

    companion object {
        private const val LOGOUT_SUCCESS_URL = "/login"
    }

    val authenticatedUser: UserDetails?
        get() {
            val context: SecurityContext = SecurityContextHolder.getContext()
            val principal: Any = context.authentication.principal
            return if (principal is UserDetails) principal
            else null
        }

    fun logout() {
        UI.getCurrent().page.setLocation(LOGOUT_SUCCESS_URL)
        val logoutHandler = SecurityContextLogoutHandler()
        logoutHandler.logout(
            VaadinServletRequest.getCurrent().httpServletRequest, null,
            null
        )
    }

}