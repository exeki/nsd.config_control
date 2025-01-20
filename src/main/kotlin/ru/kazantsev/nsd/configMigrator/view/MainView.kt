package ru.kazantsev.nsd.configMigrator.view

import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.formlayout.FormLayout
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.provider.ListDataProvider
import com.vaadin.flow.router.Route
import ru.kazantsev.nsd.configMigrator.data.model.Installation
import ru.kazantsev.nsd.configMigrator.data.repo.InstallationRepo
import ru.kazantsev.nsd.configMigrator.services.InstallationService
import ru.kazantsev.nsd.configMigrator.view.components.Card


//TODO унести в отдельный компонент список инсталляций
//TODO редактирование и удаление инсталляций
//TODO выброс ошибки при добавлении инсталляции

@Route(layout = MainLayout::class)
class MainView(
    val installationRepo: InstallationRepo,
    val installationService: InstallationService
) : VerticalLayout() {
    val filter: TextField = TextField("", "Type to filter")

    val addNewButton: Button = Button("Добавить инсталляцию") {
        openAddInstallationDialog()
    }

    val toolBar: HorizontalLayout = HorizontalLayout(filter, addNewButton)

    val intallationGrid = Grid(Installation::class.java)

    val dataProvider: ListDataProvider<Installation>

    init {
        add(toolBar)
        val cardLayout = HorizontalLayout()
        cardLayout.width = "100%"
        cardLayout.isSpacing = true
        cardLayout.justifyContentMode = FlexComponent.JustifyContentMode.CENTER
        cardLayout.alignItems = FlexComponent.Alignment.CENTER

        val cards: MutableList<Card> = ArrayList()
        cards.add(Card("Card 1", "This is card 1 description"))
        cards.add(Card("Card 2", "This is card 2 description"))
        cards.add(Card("Card 3", "This is card 3 description"))
        for (card in cards) {
            cardLayout.add(card)
        }

        intallationGrid.removeAllColumns()
        intallationGrid.addColumn(Installation::protocol).setHeader("Протокол")
        intallationGrid.addColumn(Installation::host).setHeader("Хост")
        intallationGrid.addColumn(Installation::appVersion).setHeader("Версия")
        intallationGrid.addColumn(Installation::groovyVersion).setHeader("Версия groovy")
        intallationGrid.addColumn { if (it.archived) "Yes" else "No" }.setHeader("В архиве")

        // Если необходимо отображать информацию о связях, например, `lastFromMigrationLog` и `lastToMigrationLog`
        //grid.addColumn { it.lastFromMigrationLog ?: "N/A" }.setHeader("Last From Migration Log")
        //grid.addColumn { it.lastToMigrationLog ?: "N/A" }.setHeader("Last To Migration Log")

        dataProvider = ListDataProvider<Installation>(installationRepo.findAll().toList())
        intallationGrid.dataProvider = dataProvider

        add(intallationGrid)
    }

    private fun openAddInstallationDialog() {
        val dialog = Dialog()

        val protocolField = TextField("Протокол")
        val hostField = TextField("Хост")
        val accessKeyField = TextField("Ключ")

        val saveButton = Button("Сохранить") {
            var installation = Installation(protocolField.value, hostField.value, accessKeyField.value)
            installation = installationService.updateInstallation(installation)
            dataProvider.items.add(installation)
            dataProvider.refreshAll()
            dialog.close()
        }

        val cancelButton = Button("Отмена") { dialog.close() }

        val formLayout = FormLayout()
        formLayout.add(
            protocolField,
            hostField,
            accessKeyField,
            saveButton,
            cancelButton
        )

        dialog.add(formLayout)
        dialog.open()
    }
}