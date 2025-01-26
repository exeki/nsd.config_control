package ru.kazantsev.nsd.configMigrator.ui.views

import com.vaadin.flow.component.UI
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.formlayout.FormLayout
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.html.H3
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.binder.Binder
import com.vaadin.flow.data.provider.ListDataProvider
import com.vaadin.flow.router.Route
import com.vaadin.flow.spring.annotation.UIScope
import com.vaadin.flow.spring.annotation.VaadinSessionScope
import ru.kazantsev.nsd.configMigrator.data.model.Installation
import ru.kazantsev.nsd.configMigrator.data.repo.InstallationRepo
import ru.kazantsev.nsd.configMigrator.services.InstallationService
import ru.kazantsev.nsd.configMigrator.ui.MainLayout
import ru.kazantsev.nsd.configMigrator.ui.NotificationUtils.Companion.showNotification
import ru.kazantsev.nsd.configMigrator.ui.components.FormErrorNotification

@UIScope
@VaadinSessionScope
@Route(layout = MainLayout::class)
class MainView(
    private val installationRepo: InstallationRepo,
    private val installationService: InstallationService
) : VerticalLayout() {

    private val filter: TextField = TextField("", "Type to filter")

    private val addNewButton: Button = Button("Добавить инсталляцию") {
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
                    HorizontalLayout(
                        Button("Сохранить") {
                            val installation = Installation()
                            if (!binder.writeBeanIfValid(installation)) showNotification("Пожалуйста, заполните все обязательные поля!")
                            else {
                                try {
                                    gridDataProvider.items.add(installationService.updateInstallation(installation))
                                    gridDataProvider.refreshAll()
                                    dialog.close()
                                    showNotification("Инсталляция \"${installation.host}\" успешно сохранена.")
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
    }

    private val toolBar: HorizontalLayout = HorizontalLayout(filter, addNewButton)

    private val gridDataProvider: ListDataProvider<Installation> = ListDataProvider(installationRepo.findAll().toList())

    private val installationGrid: Grid<Installation> = Grid(Installation::class.java).apply {
        dataProvider = gridDataProvider
    }

    init {

        add(toolBar)

        installationGrid.apply {
            removeAllColumns()
            addColumn(Installation::id).setHeader("ID")
            addColumn(Installation::protocol).setHeader("Протокол")
            addColumn(Installation::host).setHeader("Хост")
            addColumn(Installation::appVersion).setHeader("Версия")
            addColumn(Installation::groovyVersion).setHeader("Версия groovy")
            addColumn { if (it.archived) "Да" else "Нет" }.setHeader("В архиве")
            addItemClickListener { event ->
                UI.getCurrent().navigate(InstallationView::class.java, event.item.id)
            }
        }

        add(installationGrid)


    }
}