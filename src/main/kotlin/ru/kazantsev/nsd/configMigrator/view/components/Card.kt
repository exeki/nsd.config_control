package ru.kazantsev.sportiksmonitor.view.components

import com.vaadin.flow.component.html.Image
import com.vaadin.flow.component.html.NativeLabel
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.VerticalLayout


class Card(private val title: String, private val description: String) :
    VerticalLayout() {
    init {
        createCard()
    }

    private fun createCard() {
        isSpacing = false
        setSizeFull()


        val titleLabel = NativeLabel(title)
        titleLabel.style.set("font-size", "1.2em")
        titleLabel.style.set("font-weight", "bold")

        val descriptionLabel = NativeLabel(description)
        descriptionLabel.style.set("font-size", "0.8em")

        add(titleLabel, descriptionLabel)
        alignItems = FlexComponent.Alignment.CENTER
        style["border"] = "1px solid lightgrey"
        style["padding"] = "1em"
        style["background-color"] = "white"
        style["box-shadow"] = "2px 2px 5px lightgrey"
        style["border-radius"] = "5px"
        style["cursor"] = "pointer"
    }
}
