package com.example.sawitprotest.addticket

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sawitprotest.model.Ticket
import com.example.sawitprotest.repositories.SawitRepository
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddTicketViewModel(val repository: SawitRepository) : ViewModel() {

    private val _isDone = MutableStateFlow(false)
    val isDone = _isDone.asStateFlow()

    fun submitNewTicket(
        driverName: String,
        licenseNumber: String,
        inboundWeight: Int,
        outboundWeight: Int = 0
    ){
        _isDone.value = false
        val ticket = Ticket(
            datetime = SimpleDateFormat("yyyy-MMM-dd HH:mm", Locale("id", "ID")).format(
                Date()
            ),
            driverName = driverName,
            licenseNumber = licenseNumber,
            inboundWeight = inboundWeight,
            outboundWeight = outboundWeight
        )
        viewModelScope.launch {
            repository.submitTicket(ticket)
            _isDone.value = true
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }
}