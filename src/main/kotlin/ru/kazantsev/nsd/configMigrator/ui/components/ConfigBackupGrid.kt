package ru.kazantsev.nsd.configMigrator.ui.components

import com.vaadin.flow.component.UI
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.data.provider.ListDataProvider
import com.vaadin.flow.data.renderer.ComponentRenderer
import ru.kazantsev.nsd.configMigrator.data.model.ConfigBackup
import ru.kazantsev.nsd.configMigrator.ui.views.ConfigBackupView
import ru.kazantsev.nsd.configMigrator.ui.views.InstallationView

@Deprecated("Легче сделать на месте")
class ConfigBackupGrid(
    private val dataProvider: ListDataProvider<ConfigBackup>
) : Grid<ConfigBackup>(ConfigBackup::class.java) {

    init {


        removeAllColumns()
        addColumn(ConfigBackup::id).setHeader("ID")
        addColumn(ConfigBackup::createdDate).setHeader("Дата создания")
        /*
        addColumn(
            ComponentRenderer { configBackup: ConfigBackup ->
                Button(configBackup.installation.host) {
                    UI.getCurrent().navigate(InstallationView::class.java, configBackup.installation.id)
                }
            }
        ).setHeader("Инсталляция")
        */
        addColumn({ it.type.title }).setHeader("Тип")

        addItemClickListener { event ->
            UI.getCurrent().navigate(ConfigBackupView::class.java, event.item.id)
        }

        setItems(dataProvider)

    }
}