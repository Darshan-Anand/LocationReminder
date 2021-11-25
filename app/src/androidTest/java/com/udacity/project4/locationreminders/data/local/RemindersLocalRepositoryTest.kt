package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.locationreminders.data.dto.Result
import com.udacity.project4.locationreminders.utils.MainCoroutineRule
import com.udacity.project4.locationreminders.validReminderDTO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.Objects.isNull

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Medium Test to test the repository
@MediumTest
class RemindersLocalRepositoryTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var dB: RemindersDatabase
    private lateinit var repository: RemindersLocalRepository

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @Before
    fun initDbAndRepository() {
        dB = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).build()
        repository = RemindersLocalRepository(dB.reminderDao(), Dispatchers.Main)
    }

    @After
    fun closeDb() {
        dB.close()
    }

    @Test
    fun saveReminder() = runBlocking {

        //Given
        val validReminderDTO = validReminderDTO

        //When
        repository.saveReminder(validReminderDTO)
        val retrievedReminderDTO = repository.getReminder(validReminderDTO.id)

        //Then
        assertThat(retrievedReminderDTO is Result.Success, notNullValue())
        retrievedReminderDTO as Result.Success
        assertThat(retrievedReminderDTO.data.title, `is`(validReminderDTO.title))
        assertThat(retrievedReminderDTO.data.description, `is`(validReminderDTO.description))
        assertThat(retrievedReminderDTO.data.latitude, `is`(validReminderDTO.latitude))
        assertThat(retrievedReminderDTO.data.longitude, `is`(validReminderDTO.longitude))
        assertThat(retrievedReminderDTO.data.location, `is`(validReminderDTO.location))
    }

    @Test
    fun getReminder_DataNotFound() = runBlocking{
        val validReminderDTO = validReminderDTO
        val retrievedReminderDTO = repository.getReminder(validReminderDTO.id)
        assertThat(retrievedReminderDTO is Result.Error, notNullValue())
        retrievedReminderDTO as Result.Error
        assertThat(retrievedReminderDTO.message, `is`("Reminder not found!"))
    }

}