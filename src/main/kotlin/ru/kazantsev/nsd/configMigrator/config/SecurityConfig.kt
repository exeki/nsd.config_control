package ru.kazantsev.nsd.configMigrator.config

import com.vaadin.flow.spring.security.VaadinWebSecurity
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import ru.kazantsev.nsd.configMigrator.ui.views.LoginView

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val authenticationManager: AuthenticationManagerImpl
) : VaadinWebSecurity() {

    override fun configure(web: WebSecurity) = super.configure(web)

    override fun configure(http: HttpSecurity) {
        http.authorizeHttpRequests { auth ->
            auth.requestMatchers(AntPathRequestMatcher("/public/**")).permitAll()
        }
        http.authenticationManager(authenticationManager)
        super.configure(http)
        setLoginView(http, LoginView::class.java)
    }

}
