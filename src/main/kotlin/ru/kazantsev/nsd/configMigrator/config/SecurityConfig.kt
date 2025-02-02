package ru.kazantsev.nsd.configMigrator.config

import com.vaadin.flow.spring.security.VaadinWebSecurity
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.core.userdetails.User
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.security.provisioning.UserDetailsManager
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import ru.kazantsev.nsd.configMigrator.data.repo.UserAuthorityRepo
import ru.kazantsev.nsd.configMigrator.ui.views.LoginView


@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val userAuthorityRepo: UserAuthorityRepo
) : VaadinWebSecurity() {

    override fun configure(http: HttpSecurity) {
        http.authorizeHttpRequests { auth ->
            auth.requestMatchers(AntPathRequestMatcher("/public/**")).permitAll()
        }
        super.configure(http)
        setLoginView(http, LoginView::class.java)
    }

    public override fun configure(web: WebSecurity) {
        super.configure(web)
    }

    //@Bean
    fun userDetailsService(): UserDetailsManager {
        val user1 = User.builder().password("{noop}admin").username("admin").build()
        val user2 = User.builder().password("{noop}user").username("user").build()
        val manager = InMemoryUserDetailsManager()
        manager.createUser(user1)
        manager.createUser(user2)
        return manager
    }



}
