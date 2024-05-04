package com.example.sawitprotest.edit

import android.os.Bundle
import android.view.inputmethod.EditorInfo
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.sawitprotest.R
import com.example.sawitprotest.databinding.ActivityEditTicketBinding
import com.example.sawitprotest.model.Ticket
import com.example.sawitprotest.repositories.SawitRepository
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class EditTicketActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditTicketBinding
    private lateinit var viewModel: EditTicketViewModel

    private var ticket: Ticket? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityEditTicketBinding.inflate(layoutInflater)

        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        setupViewModel()
        setupView()
        setupToolbar()
        bindStatusUpdate()
    }

    private fun setupViewModel() {
        val repository = SawitRepository()
        val factory = MyViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[EditTicketViewModel::class.java]
    }

    private fun setupToolbar(){
        binding.editTicketToolbar.setNavigationOnClickListener { finish() }
        binding.editTicketToolbar.setOnMenuItemClickListener {menuItem ->
            when (menuItem.itemId) {
                R.id.menu_save_ticket -> {
                    if (validate()) {
                        ticket?.driverName = binding.driverEt.text.toString()
                        ticket?.inboundWeight = binding.inweightEt.text.toString().toInt()
                        ticket?.licenseNumber = binding.platEt.text.toString()
                        ticket?.outboundWeight = binding.outweightEt.text.toString().toInt()
                        ticket?.netWeight = binding.netweightEt.text.toString().toInt()

                        viewModel.submitEdittedTicket(
                            ticket!!
                        )
                    }
                    true
                }

                else -> false
            }
        }
    }

    private fun validate(): Boolean {
        val isValidDriverName = binding.driverEt.text.toString().isNotBlank()
        if (!isValidDriverName) binding.driverEt.setError("Must not empty")

        val isValidInboundWeight = binding.inweightEt.text.toString().isNotBlank()
        if (!isValidInboundWeight) binding.inweightEt.setError("Must not empty")

        val isValidPlat = binding.platEt.text.toString().isNotBlank()
        if (!isValidPlat) binding.platEt.setError("Must not empty")

        return isValidDriverName && isValidInboundWeight && isValidPlat
    }

    private fun setupView(){
        ticket = intent.getParcelableExtra("ticket")
        binding.datetimeEt.setText(ticket?.datetime)
        binding.driverEt.setText(ticket?.driverName)
        binding.platEt.setText(ticket?.licenseNumber)
        binding.inweightEt.setText(ticket?.inboundWeight.toString())
        binding.outweightEt.setText(ticket?.outboundWeight.toString())
        viewModel.setNetWeight(ticket?.netWeight ?: 0)

        binding.outweightEt.setOnEditorActionListener { _, id, _ ->
            if (id == EditorInfo.IME_ACTION_DONE) {
                calculateNetWeight()
            }
            true
        }
    }

    private fun calculateNetWeight(){
        val inWeightStr = binding.inweightEt.text.toString()
        val inWeight = if(inWeightStr.isBlank()) 0 else inWeightStr.toInt()
        val outWeightStr = binding.outweightEt.text.toString()
        val outWeight = if(outWeightStr.isBlank()) 0 else outWeightStr.toInt()
        viewModel.calculateNetWeight(inWeight, outWeight)
    }

    private fun bindStatusUpdate() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.isDone.collect {
                        if (it) finish()
                    }
                }
                launch {
                    viewModel.netWeight.collect{
                        binding.netweightEt.setText(it.toString())
                    }
                }
            }

        }
    }
    override fun onDestroy() {
        super.onDestroy()
        lifecycleScope.cancel()
    }

    class MyViewModelFactory(private val repository: SawitRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(EditTicketViewModel::class.java)) {
                return EditTicketViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}