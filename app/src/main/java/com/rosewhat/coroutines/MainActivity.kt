package com.rosewhat.coroutines

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.rosewhat.coroutines.databinding.ActivityMainBinding
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {
    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private val viewModel by lazy {
        ViewModelProvider(this)[MainViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.button.setOnClickListener {
            binding.button.isEnabled = false
            binding.progressBar.isVisible = true

            val jobCity = coroutineScope.async {
                val city = loadCity()
                binding.tvLocation.text = city
                city
            }
            val jobTemp = coroutineScope.async {
                val temperature = loadTemperature()
                binding.tvTemperature.text = temperature.toString()
                temperature

            }

            coroutineScope.launch {
                val city = jobCity.await()
                val temp = jobTemp.join()
                binding.button.isEnabled = true
                binding.progressBar.isVisible = false
                Toast.makeText(this@MainActivity, "Loading $city $temp", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private suspend fun loadData() {
        binding.progressBar.isVisible = true
        binding.button.isEnabled = false
        val city = loadCity()
        binding.tvLocation.text = city
        val temp = loadTemperature()
        binding.tvTemperature.text = temp.toString()
        binding.progressBar.isVisible = false
        binding.button.isEnabled = true

    }

    private suspend fun loadCity(): String {
        delay(5000)
        return "Moscow"
    }

    private suspend fun loadTemperature(): Int {
        delay(5000)
        return 17
    }
}