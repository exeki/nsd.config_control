package ru.kazantsev.nsd.configMigrator.services

import com.vaadin.flow.component.UI
import com.vaadin.flow.server.VaadinServletRequest
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler
import org.springframework.stereotype.Component
import ru.kazantsev.nsd.configMigrator.data.model.User
import ru.kazantsev.nsd.configMigrator.data.repo.UserRepo

@Component
class SecurityService(
    private val userRepo: UserRepo
) {

    companion object {
        private const val LOGOUT_SUCCESS_URL = "/login"
    }

    val logger: Logger = LoggerFactory.getLogger(SecurityService::class.java.name)

    val context: SecurityContext
        get() = SecurityContextHolder.getContext()

    val authentication: Authentication
        get() = context.authentication

    val authenticatedUser: User?
        get() {
            val principal: Any = authentication.principal
            return if (principal is User) {
                if (principal.id != null) principal
                else userRepo.findByUsername(principal.username).orElse(null)
            } else null
        }

    val isAdmin: Boolean
        get() {
            if (authenticatedUser == null) return false
            return authenticatedUser!!.authorities.any { it.authority == "admin" }
        }

    fun logout() {
        UI.getCurrent().page.setLocation(LOGOUT_SUCCESS_URL)
        val logoutHandler = SecurityContextLogoutHandler()
        logoutHandler.logout(VaadinServletRequest.getCurrent().httpServletRequest, null, null)
    }

    fun isUser(user: User): Boolean {
        return authenticatedUser?.username == user.username
    }

}