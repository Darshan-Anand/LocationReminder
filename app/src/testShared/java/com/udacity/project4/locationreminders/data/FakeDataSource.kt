package com.udacity.project4.locationreminders.data

import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result

//Use FakeDataSource that acts as a test double to the LocalDataSource
class FakeDataSource : ReminderDataSource {

    private var reminderDataItems: MutableList<ReminderDTO> = mutableListOf()

    private var shouldReturnError = false

    fun setShouldReturnError(shouldReturn: Boolean) {
        this.shouldReturnError = shouldReturn
    }

    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        if (shouldReturnError) {
            return Result.Error("Reminders not found")
        } else {
            return Result.Success(reminderDataItems)
        }
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        reminderDataItems.add(reminder)
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        if (shouldReturnError) {
            return Result.Error("Reminder not found ")
        } else {
            val reminder = reminderDataItems.find {
                it.id == id
            }
            if (reminder != null) {
                return Result.Success(reminder)
            } else {
                return Result.Error("Not Found")
            }

        }
    }

    override suspend fun deleteAllReminders() {
        reminderDataItems.clear()
    }


}