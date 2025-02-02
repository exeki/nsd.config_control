package ru.kazantsev.nsd.configMigrator.ui.views.`object`

import com.vaadin.flow.component.UI
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.checkbox.Checkbox
import com.vaadin.flow.component.combobox.ComboBox
import com.vaadin.flow.component.combobox.MultiSelectComboBox
import com.vaadin.flow.component.details.Details
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.formlayout.FormLayout
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.GridSortOrder
import com.vaadin.flow.component.html.*
import com.vaadin.flow.component.notification.Notification
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.data.binder.Binder
import com.vaadin.flow.data.provider.ListDataProvider
import com.vaadin.flow.data.provider.SortDirection
import com.vaadin.flow.router.*
import com.vaadin.flow.spring.annotation.UIScope
import com.vaadin.flow.spring.annotation.VaadinSessionScope
import jakarta.annotation.security.PermitAll
import ru.kazantsev.nsd.configMigrator.data.model.ConfigBackup
import ru.kazantsev.nsd.configMigrator.data.model.Installation
import ru.kazantsev.nsd.configMigrator.data.model.InstallationGroup
import ru.kazantsev.nsd.configMigrator.data.model.MigrationLog
import ru.kazantsev.nsd.configMigrator.data.model.enums.ConfigBackupType
import ru.kazantsev.nsd.configMigrator.data.repo.*
import ru.kazantsev.nsd.configMigrator.services.ConfigBackupService
import ru.kazantsev.nsd.configMigrator.services.InstallationService
import ru.kazantsev.nsd.configMigrator.ui.MainLayout
import ru.kazantsev.nsd.configMigrator.ui.components.*
import ru.kazantsev.nsd.configMigrator.ui.utils.DateTimeFormatUtils.Companion.format
import ru.kazantsev.nsd.configMigrator.ui.views.InstallationListView
import ru.kazantsev.nsd.configMigrator.ui.views.error.Error400
import ru.kazantsev.nsd.configMigrator.ui.views.error.Error404

@UIScope
@VaadinSessionScope
@Route(value = "installation", layout = MainLayout::class)
@PermitAll
class InstallationView(
    private val installationRepo: InstallationRepo,
    private val installationService: InstallationService,
    private val configBackupService: ConfigBackupService,
    private val configBackupRepo: ConfigBackupRepo,
    private val migrationPathRepo: MigrationPathRepo,
    private val installationGroupRepo: InstallationGroupRepo,
    private val migrationLogRepo: MigrationLogRepo
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

    private lateinit var migrationLogDataProvider: ListDataProvider<MigrationLog>

    private fun renderObjectCard(obj: Installation) {
        this.removeAll()
        this.installation = obj
        this.configBackupDataProvider = ListDataProvider(configBackupRepo.findByInstallation(installation))
        this.migrationLogDataProvider = ListDataProvider(migrationLogRepo.findByFromOrToIs(installation))
        add(
            VerticalLayout(
                H2("${if (installation.archived) "АРХИВ | " else ""}Инсталляция \"${installation.host}\""),
                HorizontalLayout().apply {
                    add(
                        Button("Редактировать").apply {
                            addClickListener {
                                val dialog = Dialog()
                                val binder = Binder(Installation::class.java)
                                val errorContainer = FormErrorNotification()

                                dialog.add(
                                    FormLayout(
                                        VerticalLayout(
                                            H3("Редактировать инсталляцию"),
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
                                            MultiSelectComboBox<InstallationGroup>("Группы").apply {
                                                setSizeFull()
                                                setItems(installationGroupRepo.findAll().toList())
                                                value = installation.groups
                                                setItemLabelGenerator(InstallationGroup::title)
                                                binder.forField(this).bind(Installation::groups.name)
                                            },
                                            Checkbox("Ключевая").apply {
                                                value = installation.important
                                                binder.forField(this).bind(Installation::important.name)
                                            },
                                            Span("Для некоторых действий с ключевыми инсталляциями будут запрашиваться дополнительные подтверждения."),
                                            HorizontalLayout(
                                                Button("Сохранить").apply {
                                                    addThemeVariants(ButtonVariant.LUMO_PRIMARY)
                                                    addClickListener {
                                                        if (!binder.writeBeanIfValid(installation)) Notification.show("Пожалуйста, заполните все обязательные поля!")
                                                        else {
                                                            try {
                                                                installationService.updateInstallation(installation)
                                                                dialog.close()
                                                                UI.getCurrent().page.reload()
                                                                Notification.show("Инсталляция \"${installation.host}\" успешно сохранена.")
                                                            } catch (e: Exception) {
                                                                errorContainer.show(
                                                                    "Не удалось связаться с инсталляцией \"${installation.host}\" " +
                                                                            "для получения информации о инсталляции: ${e.message}"
                                                                )
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
                            }
                        },
                        Button("Загрузить конгигурацию").apply {
                            addClickListener {
                                val dialog = Dialog()
                                val errorContainer = FormErrorNotification()
                                val installationField = ComboBox<Installation>("Выберите инсталляцию").apply {
                                    setItems(installationRepo.findAll().filter { it.id != installation.id })
                                    setItemLabelGenerator(Installation::host)
                                    setSizeFull()
                                }
                                val hostField = TextField("Введите экшн слово").apply {
                                    setSizeFull()
                                    isRequiredIndicatorVisible = true
                                    isRequired = true
                                }
                                val overrideAllField = Checkbox("Полная перезапись")
                                overrideAllField.value = true
                                val fromBackupField = Checkbox("Бекап выбранной инсталляции")
                                fromBackupField.value = false
                                val toBackupField = Checkbox("Бекап текущей инсталляции")
                                toBackupField.value = true

                                dialog.add(
                                    FormLayout(
                                        VerticalLayout(
                                            H3("Загрузить конгигурацию"),
                                            errorContainer,
                                            Div(
                                                Span("Действие загрузит конфигурацию "),
                                                BoldSpan("выбранной инсталляции"),
                                                Span(" на "),
                                                BoldSpan("текущую инсталляцию"),
                                                Span(".")
                                            ),
                                            installationField,
                                            overrideAllField,
                                            fromBackupField,
                                            toBackupField,
                                            if (!installation.important) Span() else Div(
                                                Span("Так как эта инсталляция ключевая, нужно подтвердить действие введя хост текущей инсталляции ("),
                                                BoldSpan(installation.host), Span(").")
                                            ),
                                            if (!installation.important) Span() else hostField,
                                            HorizontalLayout(
                                                Button("Загрузить!").apply {
                                                    addThemeVariants(ButtonVariant.LUMO_PRIMARY)
                                                    addClickListener {
                                                        if (installationField.value == null) Notification.show("Выберите инсталляцию!")
                                                        if (hostField.value != installation.host) errorContainer.show("Необходимо подтвердить действие, введя наименование инсталляции")
                                                        else {
                                                            try {
                                                                val log = installationService.startMigration(
                                                                    installationField.value,
                                                                    installation,
                                                                    overrideAllField.value,
                                                                    fromBackupField.value,
                                                                    toBackupField.value
                                                                )
                                                                if (toBackupField.value) {
                                                                    configBackupDataProvider.items.add(log.toBackup)
                                                                    configBackupDataProvider.refreshAll()
                                                                }
                                                                dialog.close()
                                                                //UI.getCurrent().page.reload()
                                                                Notification.show("Миграция началась.")
                                                            } catch (e: Exception) {
                                                                errorContainer.show(
                                                                    "Что то пошло не так: ${e.message}"
                                                                )
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
                            }
                        },
                        Button("Отправить конгигурацию").apply {
                            addClickListener {
                                val dialog = Dialog()
                                val errorContainer = FormErrorNotification()
                                val installationField = ComboBox<Installation>("Выберите инсталляцию").apply {
                                    setItems(installationRepo.findAll().filter { it.id != installation.id })
                                    setItemLabelGenerator(Installation::host)
                                    setSizeFull()
                                }
                                val overrideAllField = Checkbox("Полная перезапись")
                                overrideAllField.value = true
                                val toBackupField = Checkbox("Бекап выбранной инсталляции")
                                toBackupField.value = true
                                val fromBackupField = Checkbox("Бекап текущей инсталляции")
                                fromBackupField.value = false

                                dialog.add(
                                    FormLayout(
                                        VerticalLayout(
                                            H3("Отправить конгигурацию"),
                                            errorContainer,
                                            Div(
                                                Span("Действие отправит конфигурацию "),
                                                BoldSpan("текущей инсталляции"),
                                                Span(" на "),
                                                BoldSpan("выбранную инсталляцию"),
                                                Span(".")
                                            ),
                                            installationField,
                                            overrideAllField,
                                            fromBackupField,
                                            toBackupField,
                                            HorizontalLayout(
                                                Button("Отправить!").apply {
                                                    addThemeVariants(ButtonVariant.LUMO_PRIMARY)
                                                    addClickListener {
                                                        if (installationField.value == null) Notification.show("Выберите инсталляцию!")
                                                        else {
                                                            try {
                                                                val log = installationService.startMigration(
                                                                    installation,
                                                                    installationField.value,
                                                                    overrideAllField.value,
                                                                    fromBackupField.value,
                                                                    toBackupField.value
                                                                )
                                                                if (fromBackupField.value) {
                                                                    configBackupDataProvider.items.add(log.fromBackup)
                                                                    configBackupDataProvider.refreshAll()
                                                                }
                                                                dialog.close()
                                                                //UI.getCurrent().page.reload()
                                                                Notification.show("Миграция началась.")
                                                            } catch (e: Exception) {
                                                                errorContainer.show(
                                                                    "Что то пошло не так: ${e.message}"
                                                                )
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
                            }
                        },
                        Button("Сделать бекап").apply {
                            addClickListener {
                                val errorContainer = FormErrorNotification()
                                val noteField = TextField("Заметка").apply {
                                    setSizeFull()
                                    style.setMarginTop("10px")
                                }
                                val keyField = Checkbox("Ключевой бекап").apply {
                                    value = false
                                    style.setMarginBottom("20px")
                                }
                                val dialog = Dialog()
                                dialog.apply {
                                    this.add(
                                        FormLayout().apply {
                                            style.setWidth("500px")
                                            this.add(
                                                H3("Сделать бекап"),
                                                noteField,
                                                keyField,
                                                HorizontalLayout(
                                                    Button("Сохранить").apply {
                                                        addThemeVariants(ButtonVariant.LUMO_PRIMARY)
                                                        addClickListener {
                                                            try {
                                                                var backup = configBackupService.fetchAndCreateBackup(
                                                                    installation,
                                                                    ConfigBackupType.HAND
                                                                )
                                                                backup.note = noteField.value
                                                                backup.key = keyField.value
                                                                backup = configBackupRepo.save(backup)
                                                                configBackupDataProvider.items.add(backup)
                                                                configBackupDataProvider.refreshAll()
                                                                Notification.show("Бекап ${backup.title} создан")
                                                                dialog.close()
                                                            } catch (e: Exception) {
                                                                errorContainer.show("Произошла ошибка: ${e.message}")
                                                            }
                                                        }
                                                    },
                                                    Button("Отмена") { dialog.close() }
                                                )
                                            )
                                        }
                                    )
                                }

                                dialog.open()


                            }
                        },
                        if (installation.archived) Button("Восстановить из архива").apply {
                            addClickListener {
                                try {
                                    installation.archived = false
                                    installationRepo.save(installation)
                                    UI.getCurrent().page.reload()
                                } catch (e: Exception) {
                                    Notification.show("Произошла ошибка при архивации: ${e.message}")
                                }
                            }
                        }
                        else Button("Архивировать").apply {
                            addClickListener {
                                val dialog = Dialog()
                                val errorContainer = FormErrorNotification()

                                val hostField = TextField("Введите экшн слово").apply {
                                    isRequiredIndicatorVisible = true
                                    isRequired = true
                                    setSizeFull()
                                }

                                dialog.add(
                                    FormLayout(
                                        VerticalLayout(
                                            H3("Архивировать инсталляцию"),
                                            errorContainer,
                                            Div().apply {
                                                setSizeFull()
                                                add(
                                                    Span("Вместе с инсталляцией будут отправлены в архив все пути миграций, "),
                                                    Span("связанные с инсталляцией. "),
                                                    Span("Введите наименование текущий инсталляции ("),
                                                    BoldSpan(installation.host),
                                                    Span(") для продолжения:")
                                                )
                                            },
                                            hostField,
                                            HorizontalLayout(
                                                Button("Архивировать") {
                                                    if (hostField.value != installation.host) errorContainer.show("Указан неправильный хост. Ты не уверен?")
                                                    else {
                                                        try {
                                                            migrationPathRepo.findByTo(installation).forEach {
                                                                it.archived = true
                                                                migrationPathRepo.save(it)
                                                            }
                                                            migrationPathRepo.findByFrom(installation).forEach {
                                                                it.archived = true
                                                                migrationPathRepo.save(it)
                                                            }
                                                            installation.archived = true
                                                            installationRepo.save(installation)
                                                            dialog.close()
                                                            UI.getCurrent().page.reload()
                                                        } catch (e: Exception) {
                                                            errorContainer.show(e.message!!)
                                                        }
                                                    }
                                                }.apply { addThemeVariants(ButtonVariant.LUMO_PRIMARY) },
                                                Button("Отмена") { dialog.close() }
                                            )
                                        ).apply { setSizeFull() }
                                    ).apply { style.setWidth("500px") }
                                )
                                dialog.open()
                            }
                        },
                        Button("Удалить").apply {
                            addThemeVariants(ButtonVariant.LUMO_ERROR)
                            addClickListener {
                                val dialog = Dialog()
                                val errorContainer = FormErrorNotification()

                                val hostField = TextField("Введите экшн слово")
                                hostField.isRequiredIndicatorVisible = true
                                hostField.isRequired = true
                                hostField.setSizeFull()

                                dialog.add(
                                    FormLayout(
                                        VerticalLayout(
                                            H3("Удалить инсталляцию"),
                                            errorContainer,
                                            Div(
                                                Span("При удалении будут так же удалены все бекапы, планировщики "),
                                                Span("и пути миграций, связанные с инсталляцией. Может лучше положить в архив?"),
                                                Span("Введите наименование текущий инсталляции ("),
                                                BoldSpan(installation.host),
                                                Span(") для продолжения:")
                                            ).apply { setSizeFull() },
                                            hostField,
                                            HorizontalLayout(
                                                Button("Удалить!") {
                                                    if (hostField.value != installation.host) errorContainer.show("Указан неправильный хост. Ты не уверен?")
                                                    else {
                                                        try {
                                                            installationRepo.delete(installation)
                                                            Notification.show("Объект удалён")
                                                            UI.getCurrent().navigate(InstallationListView::class.java)
                                                        } catch (e: Exception) {
                                                            errorContainer.show("Что то пошло не так при удалении. Видно не судьба, видно не судьба... Вот текст ошибки: " + e.message)
                                                        }
                                                    }
                                                }.apply { addThemeVariants(ButtonVariant.LUMO_ERROR) },
                                                Button("Отмена") { dialog.close() }
                                            )
                                        ).apply { setSizeFull() }
                                    ).apply { style.setWidth("600px") }
                                )
                                dialog.open()
                            }
                        },
                    )
                },
                HorizontalLayout().apply {
                    setSizeFull()
                    add(
                        Details(H3("Параметры")).apply {
                            style.setWidth("40%")
                            isOpened = true
                            add(
                                PropertyField("ID", installation.id),
                                PropertyField("Дата создания", installation.createdDate),
                                PropertyField("Дата изменения", installation.lastModifiedDate),
                                PropertyField("HTTP Protocol", installation.protocol),
                                PropertyField("Host", installation.host),
                                PropertyField("Ключ доступа", installation.accessKey),
                                PropertyField("Ключевая", installation.important),
                                PropertyField("Версия приложения", installation.appVersion),
                                PropertyField("Версия Groovy", installation.groovyVersion),
                                PropertyField("Группы",
                                    HorizontalLayout().apply { installation.groups.forEach { this.add(InstGroupSpan(it)) } }
                                )
                            )
                        },
                        Details(H3("Список бекапов")).apply {
                            style.setWidth("60%")
                            isOpened = true
                            add(
                                Checkbox("Показать только ключевые").apply {
                                    addValueChangeListener { event ->
                                        if (event.value) {
                                            configBackupDataProvider.items.clear()
                                            configBackupDataProvider.items.addAll(
                                                configBackupRepo.findByInstallationAndKeyIs(
                                                    installation,
                                                    true
                                                )
                                            )
                                            configBackupDataProvider.refreshAll()
                                        } else {
                                            configBackupDataProvider.items.clear()
                                            configBackupDataProvider.items.addAll(
                                                configBackupRepo.findByInstallation(
                                                    installation
                                                )
                                            )
                                            configBackupDataProvider.refreshAll()
                                        }
                                    }
                                },
                                Grid(ConfigBackup::class.java).apply {
                                    removeAllColumns()
                                    addColumn(ConfigBackup::id).setHeader("ID").setWidth("15px")
                                    val sortBy = addColumn { format(it.createdDate) }.setHeader("Дата создания")
                                    addColumn({ it.type.title }).setHeader("Тип")
                                    addColumn({ if (it.key) "Да" else "Нет" }).setHeader("Ключевой").setWidth("30px")
                                    addColumn(ConfigBackup::note).setHeader("Заметка")
                                    sort(listOf(GridSortOrder(sortBy, SortDirection.DESCENDING)))
                                    addItemClickListener { event ->
                                        UI.getCurrent().navigate(ConfigBackupView::class.java, event.item.id)
                                    }
                                    setItems(configBackupDataProvider)
                                }
                            )
                        }
                    )
                },
                VerticalLayout(
                    Details(H3("Логи миграций")).apply {
                        setSizeFull()
                        isOpened = true
                        add(
                            Grid(MigrationLog::class.java).apply {
                                //TODO Таблица берет странный размер, из за чего приходится использовать isAllRowsVisible = true
                                this.setSizeFull()
                                isAllRowsVisible = true
                                removeAllColumns()
                                addColumn(MigrationLog::id).setHeader("ID").setWidth("15px")
                                val sortBy = addColumn { format(it.createdDate) }.setHeader("Дата")
                                sort(listOf(GridSortOrder(sortBy, SortDirection.DESCENDING)))
                                addColumn { if (it.overrideAll) "Да" else "Нет" }.setHeader("Перезапись")
                                addComponentColumn {
                                    RouterLink(it.from.host, InstallationView::class.java, it.from.id)
                                }.setHeader("Откуда")
                                addComponentColumn {
                                    if (it.fromBackup == null) Span("Отсутвует")
                                    else RouterLink(
                                        it.fromBackup!!.id.toString(),
                                        ConfigBackupView::class.java,
                                        it.fromBackup!!.id
                                    )
                                }.setHeader("Бекап")
                                addComponentColumn {
                                    RouterLink(it.to.host, InstallationView::class.java, it.to.id)
                                }.setHeader("Куда")
                                addComponentColumn {
                                    if (it.toBackup == null) Span("Отсутвует")
                                    else RouterLink(
                                        it.toBackup!!.id.toString(),
                                        ConfigBackupView::class.java,
                                        it.toBackup!!.id
                                    )
                                }.setHeader("Бекап")
                                addColumn({ it.state.name }).setHeader("Статус")
                                this.dataProvider = migrationLogDataProvider
                                this.addItemClickListener { event ->
                                    UI.getCurrent().navigate(MigrationLogView::class.java, event.item.id)
                                }
                            }
                        )
                    }
                )
            )
        )
    }
}