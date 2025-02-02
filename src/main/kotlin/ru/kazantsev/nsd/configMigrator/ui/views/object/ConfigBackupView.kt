package ru.kazantsev.nsd.configMigrator.ui.views.`object`

import com.vaadin.flow.component.HtmlComponent
import com.vaadin.flow.component.UI
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.html.Anchor
import com.vaadin.flow.component.html.H2
import com.vaadin.flow.component.notification.Notification
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.dom.Element
import com.vaadin.flow.router.BeforeEvent
import com.vaadin.flow.router.HasUrlParameter
import com.vaadin.flow.router.Route
import com.vaadin.flow.router.RouterLink
import com.vaadin.flow.server.InputStreamFactory
import com.vaadin.flow.server.StreamResource
import com.vaadin.flow.spring.annotation.UIScope
import com.vaadin.flow.spring.annotation.VaadinSessionScope
import jakarta.annotation.security.PermitAll
import ru.kazantsev.nsd.configMigrator.data.model.ConfigBackup
import ru.kazantsev.nsd.configMigrator.data.repo.ConfigBackupRepo
import ru.kazantsev.nsd.configMigrator.ui.MainLayout
import ru.kazantsev.nsd.configMigrator.ui.components.PropertyField
import ru.kazantsev.nsd.configMigrator.ui.views.error.Error400
import ru.kazantsev.nsd.configMigrator.ui.views.error.Error404
import java.io.ByteArrayInputStream

@UIScope
@VaadinSessionScope
@Route(value = "config_backup", layout = MainLayout::class)
@PermitAll
class ConfigBackupView(
    private val configBackupRepo: ConfigBackupRepo
) : VerticalLayout(), HasUrlParameter<Long> {

    private lateinit var configBackup: ConfigBackup

    override fun setParameter(event: BeforeEvent?, id: Long?) {
        if (id == null) UI.getCurrent().navigate(Error400::class.java)
        else configBackupRepo.findById(id).ifPresentOrElse(
            { value -> renderObjectCard(value) },
            { UI.getCurrent().navigate(Error404::class.java) }
        )
    }

    private fun renderObjectCard(obj: ConfigBackup) {
        this.configBackup = obj

        add(
            H2("Карточка бекапа конфигурации \"${configBackup.title}\""),
            HorizontalLayout(
                Button("Удалить").apply {
                    addThemeVariants(ButtonVariant.LUMO_ERROR)
                    addClickListener { _ ->
                        configBackupRepo.delete(configBackup)
                        Notification.show("Объект удалён")
                        UI.getCurrent().navigate(InstallationView::class.java, configBackup.installation.id)
                    }
                },
                Anchor(
                    StreamResource(
                        "config.xml",
                        InputStreamFactory { ByteArrayInputStream(configBackup.configFile.content) }
                    ),
                    ""
                ).apply {
                    element.setAttribute("download", true)
                    add(Button("Скачать файл конфигурации"))
                }
            ),
            PropertyField("ID", configBackup.id),
            PropertyField("Дата создания", configBackup.createdDate),
            PropertyField(
                "Инсталляция",
                RouterLink(
                    configBackup.installation.host,
                    InstallationView::class.java,
                    configBackup.installation.id
                )
            ),
            PropertyField("Ключевой", configBackup.key),
            PropertyField("Заметка", configBackup.note),
            PropertyField("Конфигурация"),
            HtmlComponent("pre").apply {
                element.setAttribute("contenteditable", false)
                element.appendChild(
                    Element("code").apply {
                        className = "language-xml"
                        element.setText(configBackup.configFile.getContentAsString())
                    }
                )
            }
        )

    }
}