package com.example.sawitprotest.repositories

import android.util.Log
import com.example.sawitprotest.model.Ticket
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseException
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.database.getValue
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Singleton

@Singleton
class SawitRepository {

    private val tickets = mutableListOf<Ticket>()
    private val firebase = Firebase
            .database("https://sawit-pro-test-f5f89-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .getReference("tickets")

    init {
        firebase.keepSynced(true)
    }

    suspend fun fetchTickets():List<Ticket> {
        val snapshot = firebase.get().await()
        tickets.clear()

        if(snapshot.hasChildren()) {
            for (data in snapshot.children) {
                val id = data.key
                try {
                    data.getValue<Ticket>()?.let {
                        it.id = id ?: ""
                        tickets.add(it)
                    }
                } catch (e: Exception) {
                    Log.e("asdf", "${e.message}")
                }
            }
        }
        return tickets
    }

    suspend fun submitTicket(ticket: Ticket) {
        val newRef = firebase.push()
        ticket.id = newRef.key.toString()
        newRef.setValue(ticket).await()
    }

    suspend fun editTicket(ticket: Ticket) {
        val id = ticket.id
        val newRef = firebase.child(id)
        newRef.setValue(ticket).await()
    }
}