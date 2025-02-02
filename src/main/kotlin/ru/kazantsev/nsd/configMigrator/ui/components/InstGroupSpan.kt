package ru.kazantsev.nsd.configMigrator.ui.components

import com.vaadin.flow.component.html.Span
import ru.kazantsev.nsd.configMigrator.data.model.InstallationGroup

class InstGroupSpan( installationGroup: InstallationGroup) : Span(installationGroup.title) {
    init {
        style.setPadding("0px 6px").setBorderRadius("4px")
        if(!installationGroup.color.isNullOrBlank()) style
            .setBackgroundColor(installationGroup.color)
            .setColor("white")
        else style
            .setBackgroundColor("grey")
            .setColor("black")
    }
}

