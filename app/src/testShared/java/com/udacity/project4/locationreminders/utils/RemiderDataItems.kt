package com.udacity.project4.locationreminders

import com.google.android.gms.maps.model.LatLng
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem

private val bengaluru = LatLng(12.972218564836638, 77.59310502906558)
val validReminderDataItem = ReminderDataItem(
    "Title",
    "Description",
    "Bengaluru",
    bengaluru.latitude,
    bengaluru.longitude
)
val validReminderDTO = ReminderDTO(
    "Title",
    "Description",
    "Bengaluru",
    bengaluru.latitude,
    bengaluru.longitude
)

val nullReminderDataItem = ReminderDataItem(
    title = null,
    description = null,
    location = null,
    latitude = null,
    longitude = null
)

val locationNullReminderDataItem = ReminderDataItem(
    "Title",
    "Description",
    null,
    null,
    null
)

val latAndLonNullReminderDataItem = ReminderDataItem(
    "Title",
    "Description",
    "Bengaluru",
    null,
    null
)

val titleNullReminderDataItem = ReminderDataItem(
    null,
    "Description",
    "Bengaluru",
    bengaluru.latitude,
    bengaluru.longitude
)



