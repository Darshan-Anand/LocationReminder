package com.udacity.project4.locationreminders.reminderslist

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.getOrAwaitValue
import com.udacity.project4.locationreminders.utils.MainCoroutineRule
import com.udacity.project4.locationreminders.validReminderDTO
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.MockitoAnnotations
import org.robolectric.annotation.Config

@Config(maxSdk = Build.VERSION_CODES.P)
@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class RemindersListViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private lateinit var fakeDataSource: FakeDataSource
    private lateinit var remindersListViewModel: RemindersListViewModel

    @Before
    fun setupFakeDataSourceAndViewModel() {
        MockitoAnnotations.initMocks(this)
        fakeDataSource = FakeDataSource()
        remindersListViewModel = RemindersListViewModel(
            ApplicationProvider.getApplicationContext(),
            fakeDataSource
        )
    }

    @Test
    fun loadRemainders_fetchLocalReminderDataItems() = mainCoroutineRule.runBlockingTest {
        //Given
        fakeDataSource.deleteAllReminders()
        val reminderDataItem = validReminderDTO
        fakeDataSource.saveReminder(reminderDataItem)

        //When
        mainCoroutineRule.dispatcher.pauseDispatcher()
        remindersListViewModel.loadReminders()

        //Then
        assertThat(remindersListViewModel.showLoading.getOrAwaitValue(), `is`(true))
        mainCoroutineRule.resumeDispatcher()

        assertThat(remindersListViewModel.showLoading.getOrAwaitValue(), `is`(false))
        assertThat(remindersListViewModel.showNoData.getOrAwaitValue(), `is`(false))
    }

    @Test
    fun loadRemainders_withEmptyList() = mainCoroutineRule.runBlockingTest {
        //Given
        fakeDataSource.deleteAllReminders()

        //When
        remindersListViewModel.loadReminders()

        //Then
        assertThat(remindersListViewModel.showNoData.getOrAwaitValue(), `is`(true))

    }

    @Test
    fun loadReminders_whenRemindersAreUnavailable() = runBlockingTest {

        fakeDataSource.setShouldReturnError(true)
        remindersListViewModel.loadReminders()
        assertThat(
            remindersListViewModel.showSnackBar.getOrAwaitValue(),
            `is`("Reminders not found")
        )
    }
}