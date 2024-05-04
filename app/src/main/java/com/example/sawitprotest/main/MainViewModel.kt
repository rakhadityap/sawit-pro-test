package com.example.sawitprotest.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sawitprotest.model.Ticket
import com.example.sawitprotest.repositories.SawitRepository
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(private val repository: SawitRepository) : ViewModel() {

    private val originalTickets = mutableListOf<Ticket>()
    private val ticketsMutableFlow = MutableStateFlow<List<Ticket>>(listOf())
    val ticketsFlow = ticketsMutableFlow.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    private var filter: String = ""

    private var sort: SORT = SORT.DEFAULT

    enum class SORT {
        DEFAULT,
        NAME,
        DATE,
        LICENSE
    }

    fun fetchTickets() {
        _isLoading.value = true
        viewModelScope.launch {
            originalTickets.clear()
            originalTickets.addAll(repository.fetchTickets())
            if (filter.isNullOrEmpty()) {
                ticketsMutableFlow.value = originalTickets
                _isLoading.value = false
            } else {
                filterTickets(filter)
            }
        }
    }

    fun filterTickets(filter: String) {
        this.filter = filter
        if (filter.isBlank()) {
            ticketsMutableFlow.value = originalTickets
        } else {
            ticketsMutableFlow.value = originalTickets.filter { ticket ->
                ticket.driverName.lowercase().contains(filter.lowercase()) ||
                        ticket.licenseNumber.lowercase().contains(filter.lowercase()) ||
                        ticket.datetime.lowercase().contains(filter.lowercase())
            }
        }
        _isLoading.value = false
    }

    fun sortTicket(sort: SORT) {
        this.sort = sort
        if(sort == SORT.DEFAULT) {
            ticketsMutableFlow.value = originalTickets
        } else {
            ticketsMutableFlow.value = originalTickets.sortedBy {ticket ->
                when(sort) {
                    SORT.DATE -> ticket.datetime
                    SORT.NAME -> ticket.driverName
                    SORT.LICENSE -> ticket.licenseNumber
                    else -> ticket.id
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }

}