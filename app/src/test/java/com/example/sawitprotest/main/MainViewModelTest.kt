package com.example.sawitprotest.main

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.sawitprotest.model.Ticket
import com.example.sawitprotest.repositories.SawitRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`

@RunWith(AndroidJUnit4::class)
class MainViewModelTest {

    private lateinit var repository: SawitRepository

    private lateinit var viewModel: MainViewModel

    @Before
    fun setUp() {
        repository = mock(SawitRepository::class.java)
        viewModel = MainViewModel(repository)
    }

    val mockTickets = listOf(
        Ticket(driverName = "John Doe", licenseNumber = "ABC123", datetime = "2024-05-03T10:00:00"),
        Ticket(
            driverName = "Jane Smith",
            licenseNumber = "DEF456",
            datetime = "2024-05-02T15:00:00"
        )
    )

    @Test
    fun fetchTickets() = runTest {
        `when`(repository.fetchTickets()).thenReturn(mockTickets)

        val loadingJob = launch { assertEquals(true, viewModel.isLoading.value) }
        val ticketsJob = launch { assertEquals(emptyList<Ticket>(), viewModel.ticketsFlow.value) }

        viewModel.fetchTickets()

        verify(repository).fetchTickets()

        assertEquals(mockTickets, viewModel.ticketsFlow.value)
        assertEquals(false, viewModel.isLoading.value)

        loadingJob.cancel()
        ticketsJob.cancel()
    }

    @Test
    fun filterTickets() = runTest {
        val filter = "John"
        val ticketsJob = launch { assertEquals(emptyList<Ticket>(), viewModel.ticketsFlow.value) }

        viewModel.filterTickets(filter)

        assertEquals(mockTickets, viewModel.ticketsFlow.value)
    }
}