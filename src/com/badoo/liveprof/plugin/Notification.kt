package com.badoo.liveprof.plugin

import com.intellij.notification.NotificationGroup
import com.intellij.notification.NotificationType
import com.intellij.openapi.application.ApplicationManager

object Notification {
    private val notificationGroup = NotificationGroup.logOnlyGroup("Live Profiler Plugin")

    fun info(string: String) {
        ApplicationManager.getApplication().invokeLater {
            notificationGroup.createNotification(string, NotificationType.INFORMATION).notify(null)
        }
    }

    fun error(string: String) {
        ApplicationManager.getApplication().invokeLater {
            notificationGroup.createNotification(string, NotificationType.ERROR).notify(null)
        }
    }

    fun warning(string: String) {
        ApplicationManager.getApplication().invokeLater {
            notificationGroup.createNotification(string, NotificationType.WARNING).notify(null)
        }
    }

    fun debug(string: String) {
        if (Settings.getInstance().enableDebug!!) {
            info(string)
        }
    }
}