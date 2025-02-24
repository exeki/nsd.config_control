package ru.kazantsev.nsd.configMigrator.ui.views

import com.vaadin.flow.component.UI
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.checkbox.Checkbox
import com.vaadin.flow.component.combobox.ComboBox
import com.vaadin.flow.component.combobox.MultiSelectComboBox
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.formlayout.FormLayout
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.html.H3
import com.vaadin.flow.component.html.Span
import com.vaadin.flow.component.notification.Notification
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.binder.Binder
import com.vaadin.flow.data.provider.ListDataProvider
import com.vaadin.flow.router.Route
import com.vaadin.flow.router.RouterLink
import com.vaadin.flow.spring.annotation.UIScope
import com.vaadin.flow.spring.annotation.VaadinSessionScope
import jakarta.annotation.security.PermitAll
import org.slf4j.LoggerFactory
import ru.kazantsev.nsd.configMigrator.data.model.Installation
import ru.kazantsev.nsd.configMigrator.data.model.InstallationGroup
import ru.kazantsev.nsd.configMigrator.data.model.MigrationPath
import ru.kazantsev.nsd.configMigrator.data.repo.InstallationRepo
import ru.kazantsev.nsd.configMigrator.data.repo.MigrationPathRepo
import ru.kazantsev.nsd.configMigrator.ui.MainLayout
import ru.kazantsev.nsd.configMigrator.ui.components.FormErrorNotification
import ru.kazantsev.nsd.configMigrator.ui.views.`object`.InstallationView
import ru.kazantsev.nsd.configMigrator.ui.views.`object`.MigrationPathView

@UIScope
@VaadinSessionScope
@Route(value = "migration_path_list", layout = MainLayout::class)
@PermitAll
class MigrationPathList(
    private val migrationPathRepo: MigrationPathRepo,
    private val installationRepo: InstallationRepo
) : VerticalLayout() {

    private val logger = LoggerFactory.getLogger(LoginView::class.java)

    private val migrationPathDataProvider = ListDataProvider(migrationPathRepo.findByArchivedIs(false))

    private var showArchived = false

    init {
        add(
            HorizontalLayout(
                Button("Добавить ") {
                    val dialog = Dialog()
                    val binder = Binder(MigrationPath::class.java)
                    val errorContainer = FormErrorNotification()

                    dialog.add(
                        FormLayout(
                            VerticalLayout(
                                H3("Добавить стандартный путь миграции"),
                                errorContainer,
                                ComboBox<Installation>("Откуда").apply {
                                    setSizeFull()
                                    setItems(installationRepo.findByArchivedIs(false).toList())
                                    setItemLabelGenerator(Installation::host)
                                    binder.forField(this).bind(MigrationPath::from.name)
                                },
                                Checkbox("Бекап исходной инсталляции").apply {
                                    binder.forField(this).bind(MigrationPath::fromBackup.name)
                                },
                                ComboBox<Installation>("Куда").apply {
                                    setSizeFull()
                                    setItems(installationRepo.findByArchivedIs(false).toList())
                                    setItemLabelGenerator(Installation::host)
                                    binder.forField(this).bind(MigrationPath::to.name)
                                },
                                Checkbox("Бекап целевой инсталляции").apply {
                                    value = true
                                    binder.forField(this).bind(MigrationPath::toBackup.name)
                                },
                                Checkbox("Полная перезапись").apply {
                                    value = true
                                    binder.forField(this).bind(MigrationPath::overrideAll.name)
                                },
                                HorizontalLayout(
                                    Button("Сохранить").apply {
                                        addClickListener {
                                            var migrationPath = MigrationPath()
                                            if (!binder.writeBeanIfValid(migrationPath)) Notification.show("Пожалуйста, заполните все обязательные поля!")
                                            else {
                                                try {
                                                    migrationPath = migrationPathRepo.save(migrationPath)
                                                    migrationPathDataProvider.items.add(migrationPath)
                                                    migrationPathDataProvider.refreshAll()
                                                    dialog.close()
                                                    Notification.show("Стандартный путь миграции успешно сохранен.")
                                                } catch (e: Exception) {
                                                    errorContainer.show("Не удалось сохранить: ${e.message}")
                                                }
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
                Button("Показать/скрыть архивные").apply {
                    text = "Показать архивные"
                    addClickListener {
                        showArchived = !showArchived
                        if (showArchived) text = "Скрыть архивные"
                        else text = "Показать архивные"
                        migrationPathDataProvider.items.clear()
                        migrationPathDataProvider.items.addAll(migrationPathRepo.findByArchivedIs(showArchived))
                        migrationPathDataProvider.refreshAll()
                    }
                },
                ),
            Grid(MigrationPath::class.java).apply {
                removeAllColumns()
                addColumn(MigrationPath::id).setHeader("ID")
                addComponentColumn {
                    Span()
                }
                addComponentColumn {
                    RouterLink(
                        it.from.host,
                        InstallationView::class.java,
                        it.from.id
                    )
                }.setHeader("Откуда")
                addColumn { if (it.fromBackup) "Да" else "Нет" }.setHeader("Бекап исходной")
                addComponentColumn {
                    RouterLink(
                        it.to.host,
                        InstallationView::class.java,
                        it.to.id
                    )
                }.setHeader("Куда")
                addColumn { if (it.toBackup) "Да" else "Нет" }.setHeader("Бекап целевой")
                addColumn { if (it.overrideAll) "Да" else "Нет" }.setHeader("Полная перезапись")
                addItemClickListener { event ->
                    UI.getCurrent().navigate(MigrationPathView::class.java, event.item.id)
                }
                dataProvider = migrationPathDataProvider
            }
        )
    }
}