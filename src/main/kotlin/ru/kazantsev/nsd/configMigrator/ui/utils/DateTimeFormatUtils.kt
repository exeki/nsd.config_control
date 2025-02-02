package ru.kazantsev.nsd.configMigrator.ui.utils

import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*

class DateTimeFormatUtils {
    companion object {
        private const val DATE_PATTERN = "dd.MM.yyyy"
        private const val TIME_PATTERN = "HH:mm:ss"
        private const val DATE_TIME_PATTERN = "$DATE_PATTERN $TIME_PATTERN"

        private val dateFormat = SimpleDateFormat(DATE_TIME_PATTERN)
        private val localDateTimeFormat = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN)
        private val localDateFormat = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN)
        private val localTimeFormat = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN)

        fun format(date: Date): String = dateFormat.format(date)
        fun format(date: LocalDateTime): String = date.format(localDateTimeFormat)
        fun format(date: LocalDate): String = date.format(localDateFormat)
        fun format(date: LocalTime): String = date.format(localTimeFormat)
    }
}