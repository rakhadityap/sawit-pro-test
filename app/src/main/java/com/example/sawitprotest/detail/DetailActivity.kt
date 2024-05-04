package com.example.sawitprotest.detail

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.sawitprotest.R
import com.example.sawitprotest.databinding.ActivityDetailBinding
import com.example.sawitprotest.edit.EditTicketActivity
import com.example.sawitprotest.model.Ticket

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    private var ticket: Ticket? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityDetailBinding.inflate(layoutInflater)

        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupView()
        setupToolbar()
    }

    private fun setupToolbar(){
        binding.detailTicketToolbar.setNavigationOnClickListener { finish() }
        binding.detailTicketToolbar.setOnMenuItemClickListener { menuItem ->
            when(menuItem.itemId) {
                R.id.edit_ticket_menu -> {
                    val intent = Intent(this, EditTicketActivity::class.java)
                    intent.putExtra("ticket", ticket)
                    startActivity(intent)
                    finish()
                    true
                }
                else -> false
            }
        }
    }

    private fun setupView(){
        ticket = intent.getParcelableExtra("ticket")
        binding.datetimeEt.setText(ticket?.datetime)
        binding.driverEt.setText(ticket?.driverName)
        binding.platEt.setText(ticket?.licenseNumber)
        binding.inweightEt.setText(ticket?.inboundWeight.toString())
        binding.outweightEt.setText(ticket?.outboundWeight.toString())
        binding.netweightEt.setText(ticket?.netWeight.toString())
    }
}