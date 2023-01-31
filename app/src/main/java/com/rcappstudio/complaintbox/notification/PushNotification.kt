package com.rcappstudio.complaintbox.notification


data class PushNotification(
    val data: NotificationData,
    val to: String
)