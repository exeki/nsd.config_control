package ru.kazantsev.nsd.configMigrator.ui.components

import com.vaadin.flow.component.html.Span

class BoldSpan() : Span() {
    init {
        style.set("font-weight", "bold")
    }

    constructor(text: String) : this() {
        this.text = text
    }
}