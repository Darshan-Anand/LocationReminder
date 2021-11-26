package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.validReminderDTO
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Unit test the DAO
@SmallTest
class RemindersDaoTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var dB: RemindersDatabase

    @Before
    fun initDb() {
        dB = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).build()
    }

    @After
    fun closeDb() {
        dB.close()
    }

    @Test
    fun saveReminder_retrieveReminder_shouldPass() = runBlockingTest {
        //Given
        val reminderDTO = validReminderDTO
        dB.reminderDao().saveReminder(reminderDTO)

        //When
        val retrievedDTO = dB.reminderDao().getReminderById(reminderDTO.id)

        //Then
        assertThat<ReminderDTO>(retrievedDTO as ReminderDTO, notNullValue())
        assertThat(retrievedDTO.title, `is`(reminderDTO.title))
        assertThat(retrievedDTO.description, `is`(reminderDTO.description))
        assertThat(retrievedDTO.location, `is`(reminderDTO.location))
        assertThat(retrievedDTO.latitude, `is`(reminderDTO.latitude))
        assertThat(retrievedDTO.longitude, `is`(reminderDTO.longitude))
    }


    @Test
    fun saveReminder_retrieveReminder_nullReminder() = runBlockingTest {

        val retrievedDTO = dB.reminderDao().getReminderById("BA")

        assertThat(null, `is`(retrievedDTO))
    }

}