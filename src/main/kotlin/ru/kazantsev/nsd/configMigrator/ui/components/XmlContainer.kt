package ru.kazantsev.nsd.configMigrator.ui.components

import com.vaadin.flow.component.HtmlComponent
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.dom.Element

@Deprecated("Не сработало и нужно всего один раз")
class XmlContainer : Div() {
    init {
        add(HtmlComponent("pre"))
        Element("pre").apply {
            setAttribute("contenteditable", false)
            appendChild(
                Element("code")
            )
        }
    }
}