package ru.kazantsev.nsd.configMigrator.ui.views

import com.vaadin.flow.component.ClickEvent
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.details.Details
import com.vaadin.flow.component.html.H3
import com.vaadin.flow.component.html.Span
import com.vaadin.flow.component.notification.Notification
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.Route
import com.vaadin.flow.spring.annotation.UIScope
import com.vaadin.flow.spring.annotation.VaadinSessionScope
import jakarta.annotation.security.PermitAll
import org.slf4j.LoggerFactory
import ru.kazantsev.nsd.configMigrator.data.repo.InstallationRepo
import ru.kazantsev.nsd.configMigrator.data.repo.UserRepo
import ru.kazantsev.nsd.configMigrator.services.ScriptExecutionService
import ru.kazantsev.nsd.configMigrator.services.SecurityService
import ru.kazantsev.nsd.configMigrator.services.scripts.GetCurrentInstallationTimeScriptTemplate
import ru.kazantsev.nsd.configMigrator.services.scripts.Test1ScriptTemplate
import ru.kazantsev.nsd.configMigrator.ui.MainLayout


@UIScope
@VaadinSessionScope
@Route(value = "test", layout = MainLayout::class)
@PermitAll
class TestView(
    val scriptExecutionService: ScriptExecutionService,
    val securityService: SecurityService,
    val installationRepo: InstallationRepo,
    val userRepo: UserRepo,
) : VerticalLayout() {

    private fun createButtonVariant(variant: ButtonVariant): Button {
        return Button(variant.toString()).apply {
            addThemeVariants(variant)
        }
    }

    private val log = LoggerFactory.getLogger(TestView::class.java)

    init {

        val installation = installationRepo.findByHost("nsd1.exeki.local").orElseThrow()

        val user = userRepo.findByUsername("admin").orElseThrow()

        add(
            H3("Кнопочки:"),
            HorizontalLayout().apply {
                ButtonVariant.entries.forEach { this.add(createButtonVariant(it)) }
            },
            H3("Скрипты:"),
            HorizontalLayout().apply {
                add(
                    Button(Test1ScriptTemplate::class.java.simpleName) {
                        val result = scriptExecutionService.executeScript(
                            Test1ScriptTemplate(),
                            installation,
                            user
                        )
                        log.info(result)
                        Notification.show(result)
                    },
                    Button(GetCurrentInstallationTimeScriptTemplate::class.java.simpleName) {
                        val result = scriptExecutionService.executeScript(
                            GetCurrentInstallationTimeScriptTemplate(),
                            installation,
                            user
                        )
                        log.info(result)
                        Notification.show(result)
                    }
                )
            }

        )
    }
}