package ru.kazantsev.nsd.configMigrator.ui.components

import com.vaadin.flow.component.html.Span
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.router.RouterLink
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Date

class PropertyField(private val propertyName: String, private val propertyValue: Any? = null) : HorizontalLayout() {

    companion object {
        private const val DATE_PATTERN = "dd.MM.yyyy HH:mm:ss"
        private val dateFormat = SimpleDateFormat(DATE_PATTERN)
        private val formatter = DateTimeFormatter.ofPattern(DATE_PATTERN)
    }

    init {
        add(BoldSpan("$propertyName:"))
        if (propertyValue != null) {
            when (propertyValue) {
                is LocalDateTime -> add(Span(propertyValue.format(formatter)))
                is LocalTime -> add(Span(propertyValue.format(formatter)))
                is LocalDate -> add(Span(propertyValue.format(formatter)))
                is Date -> add(Span(dateFormat.format(propertyValue)))
                is Boolean -> add(Span(if (propertyValue) "Да" else "Нет"))
                is RouterLink -> add(propertyValue)
                else -> add(Span(propertyValue?.toString()))
            }
        }
    }
}

