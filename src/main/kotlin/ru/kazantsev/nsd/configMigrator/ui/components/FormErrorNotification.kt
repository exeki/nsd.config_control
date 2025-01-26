package ru.kazantsev.nsd.configMigrator.ui.components

import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.html.Span
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.dom.Style

class FormErrorNotification : VerticalLayout() {

    private val span = Span().apply {
        style.setMarginTop("20px")
            .setMargin("5px")
            .setPaddingLeft("10px")
            .setPaddingRight("10px") }

    private val closeButton = Button("×").apply {
        addClickListener { hide() }
        element.setAttribute("aria-label", "Close")
        style.setPosition(Style.Position.ABSOLUTE)
            .setTop("5px")
            .setRight("10px")
            .setBackground("none")
            .setBorder("none")
            .setCursor("pointer")
            .setColor("#721c24")
            .setPadding("0")
            .setMargin("0")
            .setHeight("15px")
            .setMinWidth("15px")
            .setWidth("15px")
    }

    init {
        isVisible = false
        style.setBackgroundColor("#FCE8E7")
            .setPadding("0")
            .setMargin("0")
            .setColor("#721c24") // Темно-красный текст
            .setBorderRadius("10px")
            .setPosition(Style.Position.RELATIVE)
        add(closeButton)
        add(span)
    }

    fun show(): FormErrorNotification {
        isVisible = true
        return this
    }

    fun show(text: String): FormErrorNotification {
        span.text = text
        return show()
    }

    fun hide(): FormErrorNotification {
        isVisible = false
        return this
    }
}