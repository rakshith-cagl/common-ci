package com.iexceed.plugins.notification

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class NotificationBody(
    val actionCode: String?,
    val notificationCode: String?,
    val message: String?,
    val notifId: Int,
    val msgParameters: String?,
    val title: String?,
    val imageUrl: String?
):Parcelable
