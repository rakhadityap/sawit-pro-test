package com.example.sawitprotest.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sawitprotest.R
import com.example.sawitprotest.addticket.AddTicketActivity
import com.example.sawitprotest.databinding.ActivityMainBinding
import com.example.sawitprotest.detail.DetailActivity
import com.example.sawitprotest.model.Ticket
import com.example.sawitprotest.repositories.SawitRepository
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), MainAdapter.AdapterHook {

    private lateinit var binding: ActivityMainBinding

    private lateinit var viewModel: MainViewModel

    private lateinit var adapter: MainAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupViewModel()
        setupToolbar()
        showLoading()
        setupRecyclerView()
        setupFilter()
        bindStateUpdates()
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchTickets()
    }

    private fun setupFilter() {
        binding.ticketSv.setOnQueryTextListener(object : OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                viewModel.filterTickets(query ?: "")
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.filterTickets(newText ?: "")
                return true
            }

        })
    }

    private fun showLoading() {
        binding.ticketRv.visibility = View.GONE
        binding.mainLoading.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        binding.ticketRv.visibility = View.VISIBLE
        binding.mainLoading.visibility = View.GONE
    }

    private fun setupViewModel() {
        val repository = SawitRepository()
        val factory = MyViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(MainViewModel::class.java)
    }

    private fun setupToolbar() {
//        setSupportActionBar(binding.toolbar)
        binding.toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_refresh -> {
                    viewModel.fetchTickets()
                    true
                }

                R.id.menu_sort_name -> {
                    viewModel.sortTicket(MainViewModel.SORT.NAME)
                    true
                }

                R.id.menu_sort_date -> {
                    viewModel.sortTicket(MainViewModel.SORT.DATE)
                    true
                }

                R.id.menu_sort_plate -> {
                    viewModel.sortTicket(MainViewModel.SORT.LICENSE)
                    true
                }

                R.id.menu_sort_reset -> {
                    viewModel.sortTicket(MainViewModel.SORT.DEFAULT)
                    true
                }

                R.id.menu_add -> {
                    val i = Intent(this, AddTicketActivity::class.java)
                    startActivity(i)
                    true
                }

                else -> false
            }
        }
    }

    private fun setupRecyclerView() {
        binding.ticketRv.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        adapter = MainAdapter(this)
        binding.ticketRv.adapter = adapter
    }

    override fun onClick(ticket: Ticket) {
        val intent = Intent(this, DetailActivity::class.java)
        intent.putExtra("ticket", ticket)
        startActivity(intent)
    }

    override fun getContext(): Context = this

    private fun bindStateUpdates() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.ticketsFlow.collect { tickets ->
                        adapter.submitList(tickets)
                        adapter.notifyDataSetChanged()
                    }
                }

                launch {
                    viewModel.isLoading.collect {
                        if (it) showLoading()
                        else hideLoading()
                    }
                }
            }
        }
    }

    class MyViewModelFactory(private val repository: SawitRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                return MainViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycleScope.cancel()
    }
}