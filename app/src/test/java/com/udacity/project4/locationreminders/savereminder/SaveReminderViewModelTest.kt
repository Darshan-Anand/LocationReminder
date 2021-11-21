package com.udacity.project4.locationreminders.savereminder

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.*
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.utils.MainCoroutineRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@Config(maxSdk = Build.VERSION_CODES.P)
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class SaveReminderViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private lateinit var fakeDataSource: FakeDataSource
    private lateinit var saveReminderViewModel: SaveReminderViewModel

    @Before
    fun setupFakeDataSourceAndViewModel() {
        fakeDataSource = FakeDataSource()
        saveReminderViewModel = SaveReminderViewModel(
            ApplicationProvider.getApplicationContext(),
            fakeDataSource
        )
    }

    @Test
    fun validateAndSaveReminder_validReminderData_returnsBoolean() {
        //Given: a ReminderDataItem
        val reminderDataItem = validReminderDataItem

        //When: validating remainderDataItem is correct
        val saved: Boolean = saveReminderViewModel.validateAndSaveReminder(reminderDataItem)

        //Then: returns true if it is correct
        assertThat(saved, `is`(true))
    }

    @Test
    fun validateAndSaveReminder_nullReminderData_returnsBoolean() {
        //Given: a ReminderDataItem
        val reminderDataItem = nullReminderDataItem

        //When: validating remainderDataItem is correct
        val saved: Boolean = saveReminderViewModel.validateAndSaveReminder(reminderDataItem)

        //Then: returns true if it is correct
        assertThat(saved, `is`(false))
    }

    @Test
    fun saveRemainder_validReminderData_showToast() {
        //Given: a RemainderDataItem
        val reminderDataItem = nullReminderDataItem

        //When
        saveReminderViewModel.saveReminder(reminderDataItem)

        //Then
        assertThat(saveReminderViewModel.showToast.getOrAwaitValue(), `is`("Reminder Saved !"))
    }

    @Test
    fun validateEnteredData_titleNullReminderData_returnsBoolean() {
        //Given: a RemainderDataItem
        val reminderDataItem = titleNullReminderDataItem

        //When
        val bool: Boolean = saveReminderViewModel.validateEnteredData(reminderDataItem)

        //Then
        assertThat(bool, `is`(false))
    }

    @Test
    fun validateEnteredData_latAndLanNullReminderData_returnsBoolean() {
        //Given: a RemainderDataItem
        val reminderDataItem = latAndLonNullReminderDataItem

        //When
        val bool: Boolean = saveReminderViewModel.validateEnteredData(reminderDataItem)

        //Then
        assertThat(bool, `is`(true))
    }

}