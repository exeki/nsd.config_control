package ru.kazantsev.nsd.configMigrator.ui

import com.vaadin.flow.component.notification.Notification

class NotificationUtils {
    companion object {
        fun showNotification(text: String) {
            showNotification(3000, text)
        }

        fun showNotification(duration: Int, text: String) {
            val notification = Notification()
            notification.duration = duration
            notification.setText(text)
            notification.open()
        }
    }
}