package com.example.sawitprotest.addticket

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.sawitprotest.R
import com.example.sawitprotest.databinding.ActivityAddTicketBinding
import com.example.sawitprotest.repositories.SawitRepository
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddTicketActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddTicketBinding

    private lateinit var viewModel: AddTicketViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityAddTicketBinding.inflate(layoutInflater)

        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupViewModel()
        bindStatusUpdate()
        setupMenu()
        setupFocus()
        binding.datetimeEt.setText(
            SimpleDateFormat("yyyy-MMM-dd HH:mm", Locale("id", "ID")).format(
                Date()
            )
        )

        binding.createTicketToolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupViewModel() {
        val repository = SawitRepository()
        val factory = MyViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[AddTicketViewModel::class.java]
    }

    private fun setupMenu() {
        binding.createTicketToolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_save_ticket -> {
                    if (validate()) {
                        val licenseNumber =
                            "${binding.plat1Et.text}${binding.plat2Et.text}${binding.plat3Et.text}"
                        viewModel.submitNewTicket(
                            driverName = binding.driverEt.text.toString(),
                            inboundWeight = binding.inweightEt.text.toString().toInt(),
                            licenseNumber = licenseNumber,
                            outboundWeight = binding.outweightEt.text.toString().toInt()
                        )
                    }
                    true
                }

                else -> false
            }
        }
    }

    private fun setupFocus() {
        binding.driverEt.setOnFocusChangeListener { view, hasFocus ->
            binding.driverEt.setError(null)
        }
        binding.plat1Et.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                binding.plat1Et.setError(null)
            }
        }
        binding.plat2Et.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                binding.plat2Et.setError(null)
            }
        }
        binding.plat3Et.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                binding.plat3Et.setError(null)
            }
        }
        binding.inweightEt.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                binding.inweightEt.setError(null)
            }
        }
    }

    private fun validate(): Boolean {
        val isValidDriverName = binding.driverEt.text.toString().isNotBlank()
        if (!isValidDriverName) binding.driverEt.setError("Must not empty")

        val isValidInboundWeight = binding.inweightEt.text.toString().isNotBlank()
        if (!isValidInboundWeight) binding.inweightEt.setError("Must not empty")

        val isValidPlat1 = binding.plat1Et.text.toString().isNotBlank()
        if (!isValidPlat1) binding.plat1Et.setError("Must not empty")

        val isValidPlat2 = binding.plat2Et.text.toString().isNotBlank()
        if (!isValidPlat2) binding.plat2Et.setError("Must not empty")

        val isValidPlat3 = binding.plat3Et.text.toString().isNotBlank()
        if (!isValidPlat3) binding.plat3Et.setError("Must not empty")

        return isValidDriverName && isValidInboundWeight && isValidPlat1 && isValidPlat2 && isValidPlat3
    }

    private fun bindStatusUpdate() {
        lifecycleScope.launch {
            viewModel.isDone.collect {
                if (it) finish()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycleScope.cancel()
    }

    class MyViewModelFactory(private val repository: SawitRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AddTicketViewModel::class.java)) {
                return AddTicketViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}