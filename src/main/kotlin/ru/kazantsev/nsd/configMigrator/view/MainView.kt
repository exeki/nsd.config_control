package ru.kazantsev.sportiksmonitor.view

import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.router.Route
import ru.kazantsev.sportiksmonitor.view.components.Card


@Route(layout = MainLayout::class)
class MainView() : VerticalLayout() {
    val filter: TextField = TextField("", "Type to filter")

    val addNewButton: Button = Button("Add New")

    val toolBar: HorizontalLayout = HorizontalLayout(filter, addNewButton)

    init {
        add(toolBar)
        val cardLayout = HorizontalLayout()
        cardLayout.width = "100%"
        cardLayout.isSpacing = true
        cardLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER)
        cardLayout.alignItems = FlexComponent.Alignment.CENTER

        val cards: MutableList<Card> = ArrayList<Card>()
        cards.add(Card("Card 1", "This is card 1 description"))
        cards.add(Card("Card 2", "This is card 2 description"))
        cards.add(Card("Card 3", "This is card 3 description"))
        for (card in cards) {
            cardLayout.add(card)
        }

    }
}