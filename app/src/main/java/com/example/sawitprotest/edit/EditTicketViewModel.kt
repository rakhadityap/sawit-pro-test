package com.example.sawitprotest.edit

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

class EditTicketViewModel(val repository: SawitRepository): ViewModel() {

    private val _isDone = MutableStateFlow(false)
    val isDone = _isDone.asStateFlow()

    private val _netWeight = MutableStateFlow(0)
    val netWeight = _netWeight.asStateFlow()

    fun setNetWeight(weight:Int){
        _netWeight.value = weight
    }

    fun submitEdittedTicket(
        ticket: Ticket
    ){
        _isDone.value = false
        viewModelScope.launch {
            repository.editTicket(ticket)
            _isDone.value = true
        }
    }

    fun calculateNetWeight(inWeight: Int, outWeight: Int){
        if (outWeight == 0 || outWeight < inWeight) _netWeight.value = 0
        else _netWeight.value = outWeight - inWeight
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }
}