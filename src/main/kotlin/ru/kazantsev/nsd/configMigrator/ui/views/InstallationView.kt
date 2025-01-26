package ru.kazantsev.nsd.configMigrator.ui.views

import com.vaadin.flow.component.UI
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.formlayout.FormLayout
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.GridSortOrder
import com.vaadin.flow.component.html.*
import com.vaadin.flow.component.notification.Notification
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.binder.Binder
import com.vaadin.flow.data.provider.ListDataProvider
import com.vaadin.flow.data.provider.SortDirection
import com.vaadin.flow.router.*
import com.vaadin.flow.spring.annotation.UIScope
import com.vaadin.flow.spring.annotation.VaadinSessionScope
import org.hibernate.engine.config.spi.ConfigurationService
import ru.kazantsev.nsd.configMigrator.data.dto.view.ConfigBackupDto
import ru.kazantsev.nsd.configMigrator.data.model.ConfigBackup
import ru.kazantsev.nsd.configMigrator.data.model.Installation
import ru.kazantsev.nsd.configMigrator.data.model.enums.ConfigBackupType
import ru.kazantsev.nsd.configMigrator.data.repo.ConfigBackupRepo
import ru.kazantsev.nsd.configMigrator.data.repo.InstallationRepo
import ru.kazantsev.nsd.configMigrator.services.ConfigBackupService
import ru.kazantsev.nsd.configMigrator.services.InstallationService
import ru.kazantsev.nsd.configMigrator.ui.MainLayout
import ru.kazantsev.nsd.configMigrator.ui.NotificationUtils.Companion.showNotification
import ru.kazantsev.nsd.configMigrator.ui.components.ConfigBackupGrid
import ru.kazantsev.nsd.configMigrator.ui.components.FormErrorNotification
import ru.kazantsev.nsd.configMigrator.ui.components.PropertyField
import ru.kazantsev.nsd.configMigrator.ui.views.error.Error400
import ru.kazantsev.nsd.configMigrator.ui.views.error.Error404

@UIScope
@VaadinSessionScope
@Route(value = "installation", layout = MainLayout::class)
class InstallationView(
    private val installationRepo: InstallationRepo,
    private val installationService: InstallationService,
    private val configBackupService: ConfigBackupService,
    private val configBackupRepo: ConfigBackupRepo,
) : VerticalLayout(), HasUrlParameter<Long> {

    private lateinit var installation: Installation

    override fun setParameter(event: BeforeEvent?, id: Long?) {
        if (id == null) UI.getCurrent().navigate(Error400::class.java)
        else installationRepo.findById(id).ifPresentOrElse(
            { value -> renderObjectCard(value) },
            { UI.getCurrent().navigate(Error404::class.java) }
        )
    }

    private lateinit var configBackupDataProvider: ListDataProvider<ConfigBackup>

    private fun renderObjectCard(obj: Installation) {
        this.installation = obj
        this.configBackupDataProvider = ListDataProvider(configBackupRepo.findByInstallation(installation))

        add(
            VerticalLayout(
                H2("Инсталляция \"${installation.host}\""),
                HorizontalLayout(
                    Button("Редактировать") {
                        val dialog = Dialog()
                        val binder = Binder(Installation::class.java)
                        val errorContainer = FormErrorNotification()

                        dialog.add(
                            FormLayout(
                                VerticalLayout(
                                    H3("Добавить инсталляцию"),
                                    errorContainer,
                                    TextField("Протокол").apply {
                                        setSizeFull()
                                        isRequiredIndicatorVisible = true
                                        value = installation.protocol
                                        binder.forField(this).asRequired().bind(Installation::protocol.name)
                                    },
                                    TextField("Хост").apply {
                                        setSizeFull()
                                        isRequiredIndicatorVisible = true
                                        value = installation.host
                                        binder.forField(this).asRequired().bind(Installation::host.name)
                                    },
                                    TextField("Ключ").apply {
                                        setSizeFull()
                                        isRequiredIndicatorVisible = true
                                        value = installation.accessKey
                                        binder.forField(this).asRequired().bind(Installation::accessKey.name)
                                    },
                                    HorizontalLayout(
                                        Button("Сохранить") {
                                            if (!binder.writeBeanIfValid(installation)) showNotification("Пожалуйста, заполните все обязательные поля!")
                                            else {
                                                try {
                                                    installationService.updateInstallation(installation)
                                                    dialog.close()
                                                    UI.getCurrent().page.reload()
                                                    showNotification("Инсталляция \"${installation.host}\" успешно сохранена.")
                                                } catch (e: Exception) {
                                                    errorContainer.show(
                                                        "Не удалось связаться с инсталляцией \"${installation.host}\" " +
                                                                "для получения информации о инсталляции: ${e.message}"
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
                    },
                    Button("Удалить") { _ ->
                        installationRepo.delete(installation)
                        Notification.show("Объект удалён")
                        UI.getCurrent().navigate(MainView::class.java)
                    }.apply { addThemeVariants(ButtonVariant.LUMO_ERROR) },
                    Button("Выгрузить бекап") {
                        val backup = configBackupService.fetchAndCreateBackup(installation, ConfigBackupType.HAND)
                        configBackupDataProvider.items.add(backup)
                        configBackupDataProvider.refreshAll()
                    }
                ),
                HorizontalLayout(
                    VerticalLayout(
                        H3("Параметры:"),
                        PropertyField("ID", installation.id),
                        PropertyField("Дата создания", installation.createdDate),
                        PropertyField("Дата изменения", installation.lastModifiedDate),
                        PropertyField("HTTP Protocol", installation.protocol),
                        PropertyField("Host", installation.host),
                        PropertyField("Ключ доступа", installation.accessKey),
                        PropertyField("Версия приложения", installation.appVersion),
                        PropertyField("Версия Groovy", installation.groovyVersion)
                    ),
                    VerticalLayout(
                        H3("Список бекапов:"),
                        Grid(ConfigBackup::class.java).apply {
                            removeAllColumns()
                            addColumn(ConfigBackup::id).setHeader("ID")
                            val sortBy = addColumn(ConfigBackup::createdDate).setHeader("Дата создания")
                            addColumn({ it.type.title }).setHeader("Тип")
                            sort(listOf(GridSortOrder(sortBy, SortDirection.DESCENDING)))
                            addItemClickListener { event ->
                                UI.getCurrent().navigate(ConfigBackupView::class.java, event.item.id)
                            }
                            setItems(configBackupDataProvider)
                        }
                    )
                ).apply { setSizeFull() }
            )
        )
    }
}