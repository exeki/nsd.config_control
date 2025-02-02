package ru.kazantsev.nsd.configMigrator.ui.views

import com.vaadin.flow.component.UI
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.checkbox.Checkbox
import com.vaadin.flow.component.combobox.MultiSelectComboBox
import com.vaadin.flow.component.details.Details
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
import com.vaadin.flow.spring.annotation.UIScope
import com.vaadin.flow.spring.annotation.VaadinSessionScope
import jakarta.annotation.security.PermitAll
import ru.kazantsev.nsd.configMigrator.data.model.Installation
import ru.kazantsev.nsd.configMigrator.data.model.InstallationGroup
import ru.kazantsev.nsd.configMigrator.data.repo.InstallationGroupRepo
import ru.kazantsev.nsd.configMigrator.data.repo.InstallationRepo
import ru.kazantsev.nsd.configMigrator.services.InstallationService
import ru.kazantsev.nsd.configMigrator.ui.MainLayout
import ru.kazantsev.nsd.configMigrator.ui.components.FormErrorNotification
import ru.kazantsev.nsd.configMigrator.ui.components.InstGroupSpan
import ru.kazantsev.nsd.configMigrator.ui.views.`object`.InstallationView


@UIScope
@VaadinSessionScope
@Route(value = "installations", layout = MainLayout::class)
@PermitAll
class InstallationListView(
    private val installationRepo: InstallationRepo,
    private val installationService: InstallationService,
    private val installationGroupRepo: InstallationGroupRepo
) : VerticalLayout() {

    private var showArchived = false

    private val filter = TextField("", "Поиск по хосту").apply {
        addValueChangeListener { reloadInstallationGridData() }
    }

    private val installationGridDataProvider = ListDataProvider(installationRepo.findByArchivedIs(false))

    private fun reloadInstallationGridData() {
        installationGridDataProvider.items.clear()
        installationGridDataProvider.items.addAll(
            if (installationGroupGrid.selectedItems.isEmpty()) {
                if (filter.value.isNullOrBlank()) installationRepo.findByArchivedIs(showArchived)
                else installationRepo.findByHostLikeAndArchivedIs("%" + filter.value + "%", showArchived)
            } else {
                if (filter.value.isNullOrBlank()) installationRepo.findByArchivedIsAndGroupsIn(
                    showArchived,
                    installationGroupGrid.selectedItems
                )
                else installationRepo.findByHostLikeAndArchivedIsAndGroupsIn(
                    "%" + filter.value + "%",
                    showArchived,
                    installationGroupGrid.selectedItems
                )
            }

        )
        installationGridDataProvider.refreshAll()
    }

    private val installationGroupGridDataProvider = ListDataProvider(installationGroupRepo.findAll().toList())

    private fun reloadInstallationGroupGridData() {
        installationGroupGridDataProvider.items.clear()
        installationGroupGridDataProvider.items.addAll(
            installationGroupRepo.findAll().toList()
        )
        installationGroupGridDataProvider.refreshAll()
    }

    private val installationGrid = Grid(Installation::class.java)

    private val installationGroupGrid = Grid(InstallationGroup::class.java)

    private val updateInstallationButton = Button("Обновить информацию")

    init {
        //Отрисовка страницы
        add(
            Details(H3("Группы инсталляций")).apply {
                isOpened = true
                setSizeFull()
                add(
                    HorizontalLayout(Button("Добавить группу").apply {
                        addClickListener {
                            val dialog = Dialog()
                            val binder = Binder(InstallationGroup::class.java)
                            val errorContainer = FormErrorNotification()
                            dialog.apply {
                                add(
                                    FormLayout(
                                        VerticalLayout(
                                            H3("Добавить группу"),
                                            errorContainer,
                                            TextField("Названия").apply {
                                                setSizeFull()
                                                isRequiredIndicatorVisible = true
                                                binder.forField(this).asRequired().bind(InstallationGroup::title.name)
                                            },
                                            TextField("Цвет").apply {
                                                setSizeFull()
                                                binder.forField(this).bind(InstallationGroup::color.name)
                                            },
                                            Span("В цвет нужно ввести валидный для css цвет (код цвета или код HEX палитры)."),
                                            HorizontalLayout(
                                                Button("Сохранить").apply {
                                                    addClickListener {
                                                        val group = InstallationGroup()
                                                        if (!binder.writeBeanIfValid(group)) Notification.show("Пожалуйста, заполните все обязательные поля!")
                                                        else {
                                                            try {
                                                                installationGroupRepo.save(group)
                                                                Notification.show("Группа ${group.title} добавлена.")
                                                                reloadInstallationGroupGridData()
                                                                dialog.close()
                                                            } catch (e: Exception) {
                                                                errorContainer.show("Не удалось сохранить группу: " + e.message)
                                                            }
                                                        }
                                                    }
                                                },
                                                Button("Отменить") { dialog.close() }
                                            )
                                        )
                                    ).apply { style.setMinWidth("500px") }
                                )
                            }
                            dialog.open()
                        }
                    }),
                    installationGroupGrid.apply {
                        isAllRowsVisible = true
                        dataProvider = installationGroupGridDataProvider
                        //style.setWidth("50%")
                        removeAllColumns()
                        //addColumn(InstallationGroup::title).setHeader("Название")
                        //setSelectionMode(Grid.SelectionMode.MULTI)
                        addComponentColumn { item ->
                            InstGroupSpan(item).apply {
                                style.setFontSize("20px")
                            }
                        }.setHeader("Название")
                        addComponentColumn { item ->
                            HorizontalLayout(
                                Button("Редактировать").apply {
                                    addThemeVariants(ButtonVariant.LUMO_SMALL)
                                    addClickListener {
                                        val dialog = Dialog()
                                        val errorContainer = FormErrorNotification()
                                        val binder = Binder(InstallationGroup::class.java)
                                        dialog.add(
                                            FormLayout().apply {
                                                style.setWidth("500px")
                                                add(
                                                    VerticalLayout(
                                                        H3("Редактировать группу"),
                                                        TextField("Название").apply {
                                                            setSizeFull()
                                                            binder.forField(this).asRequired()
                                                                .bind(InstallationGroup::title.name)
                                                            value = item.title
                                                            isRequiredIndicatorVisible = true
                                                        },
                                                        TextField("Цвет").apply {
                                                            setSizeFull()
                                                            value = item.color
                                                            binder.forField(this).bind(InstallationGroup::color.name)
                                                        },
                                                        Span("В цвет нужно ввести валидный для css цвет (код цвета или код HEX палитры)."),
                                                        HorizontalLayout(
                                                            Button("Сохранить").apply {
                                                                addThemeVariants(ButtonVariant.LUMO_PRIMARY)
                                                                addClickListener {
                                                                    if (!binder.writeBeanIfValid(item)) Notification.show(
                                                                        "Пожалуйста, заполните все обязательные поля!"
                                                                    )
                                                                    else {
                                                                        try {
                                                                            installationGroupRepo.save(item)
                                                                            Notification.show("Группа ${item.title} изменена.")
                                                                            //reloadInstallationGroupGridData()
                                                                            dialog.close()
                                                                        } catch (e: Exception) {
                                                                            errorContainer.show("Не удалось сохранить группу: " + e.message)
                                                                        }
                                                                    }

                                                                }
                                                            },
                                                            Button("Отмена") { dialog.close() }
                                                        )
                                                    )
                                                )
                                            }
                                        )
                                        dialog.open()
                                    }
                                },
                                Button("Удалить").apply {
                                    addThemeVariants(ButtonVariant.LUMO_SMALL)
                                    addThemeVariants(ButtonVariant.LUMO_ERROR)
                                    addClickListener {
                                        try {
                                            installationGroupRepo.delete(item)
                                            installationGroupGridDataProvider.items.remove(item)
                                            installationGroupGridDataProvider.refreshAll()
                                        } catch (e: Exception) {
                                            Notification.show("Не удалось удалить группу \"${item.title}\": ${e.message}")
                                        }
                                    }
                                }
                            )
                        }.setHeader("Действия")
                        addSelectionListener {
                            reloadInstallationGridData()
                        }
                    }
                )
            },
            Details(H3("Список инсталляций")).apply {
                isOpened = true
                setSizeFull()
                add(
                    HorizontalLayout(
                        filter,
                        Button("Добавить инсталляцию") {
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
                                            value = "https"
                                            binder.forField(this).asRequired().bind(Installation::protocol.name)
                                        },
                                        TextField("Хост").apply {
                                            setSizeFull()
                                            isRequiredIndicatorVisible = true
                                            binder.forField(this).asRequired().bind(Installation::host.name)
                                        },
                                        TextField("Ключ").apply {
                                            setSizeFull()
                                            isRequiredIndicatorVisible = true
                                            binder.forField(this).asRequired().bind(Installation::accessKey.name)
                                        },
                                        MultiSelectComboBox<InstallationGroup>("Группы").apply {
                                            setSizeFull()
                                            setItems(installationGroupRepo.findAll().toList())
                                            setItemLabelGenerator(InstallationGroup::title)
                                            binder.forField(this).bind(Installation::groups.name)
                                        },
                                        Checkbox("Ключевая").apply {
                                            binder.forField(this).bind(Installation::important.name)
                                        },
                                        Span("Для некоторых действий с ключевыми инсталляциями будут запрашиваться дополнительные подтверждения."),
                                        HorizontalLayout(
                                            Button("Сохранить") {
                                                var installation = Installation()
                                                if (!binder.writeBeanIfValid(installation)) Notification.show("Пожалуйста, заполните все обязательные поля!")
                                                else {
                                                    try {
                                                        installation =
                                                            installationService.updateInstallation(installation)
                                                        installationGridDataProvider.items.add(installation)
                                                        installationGridDataProvider.refreshAll()
                                                        dialog.close()
                                                        Notification.show("Инсталляция \"${installation.host}\" успешно сохранена.")
                                                    } catch (e: Exception) {
                                                        errorContainer.show(
                                                            "Не удалось связаться с инсталляцией ${installation.host} " +
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
                        Button("Показать/скрыть архивные").apply {
                            text = "Показать архивные"
                            addClickListener {
                                showArchived = !showArchived
                                if (showArchived) text = "Скрыть архивные"
                                else text = "Показать архивные"
                                reloadInstallationGridData()
                            }
                        },
                        updateInstallationButton.apply {
                            isEnabled = false
                            addClickListener {
                                var updated = 0
                                installationGrid.selectedItems.forEach {
                                    try {
                                        installationService.updateInstallation(it)
                                        updated++
                                    } catch (e: Exception) {
                                        Notification.show("Не удалось обновить инсталлцию \"${it.host}\": ${e.message}")
                                    }
                                }
                                Notification.show("Обновление выполнено")
                            }
                        }
                    ),
                    installationGrid.apply {
                        removeAllColumns()
                        addColumn(Installation::id).setHeader("ID")
                        addColumn(Installation::protocol).setHeader("Протокол")
                        addColumn(Installation::host).setHeader("Хост")
                        addColumn(Installation::appVersion).setHeader("Версия")
                        addColumn(Installation::groovyVersion).setHeader("Версия groovy")
                        //addColumn { if (it.archived) "Да" else "Нет" }.setHeader("В архиве")
                        addColumn { if (it.important) "Да" else "Нет" }.setHeader("Ключевая")
                        addComponentColumn { item ->
                            HorizontalLayout().apply {
                                item.groups.forEach { this.add(InstGroupSpan(it)) }
                            }
                        }
                        addItemClickListener { event ->
                            UI.getCurrent().navigate(InstallationView::class.java, event.item.id)
                        }
                        dataProvider = installationGridDataProvider
                        this.setSelectionMode(Grid.SelectionMode.MULTI)

                        addSelectionListener {
                            updateInstallationButton.isEnabled = selectedItems.isNotEmpty()
                        }
                    }
                )
            },
        )
    }
}