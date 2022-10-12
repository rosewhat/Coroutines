package com.rosewhat.coroutines

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import java.lang.Exception
import java.lang.RuntimeException

class MainViewModel : ViewModel() {

    private val parentJob = Job()

    // если случится ошибка, никак не влияет на другие корутины
    private val superVisorJob = SupervisorJob()
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Log.d(CR_ERROR, "Exception caught: $throwable")
    }
    private val coroutineScope =
        CoroutineScope(Dispatchers.Main + CoroutineName("My coroutine") + parentJob + exceptionHandler)

    fun method() {
        // обрабатывать ошибки с помощью CoroutineExceptionHandler
        val childJob1 = coroutineScope.launch {
            delay(3000)
            error()
        }

        // deferred - хранит в себе ошибку, в случае возник ошибки, не будет ломаться код, не нужно обраб в CoroutineExceptionHandler
        // сработает только тогда, когда await
        val childJob2 = coroutineScope.async {
            error()
        }
        coroutineScope.launch {
            try {
                childJob2.await()
            } catch (e: Exception) {

            }

        }
    }

    fun lan() {
        val job = viewModelScope.launch(Dispatchers.Default) {
            var count = 0
            val before = System.currentTimeMillis()
            for (i in 0 until 100_000_000) {
                for (j in 0 until 100) {
                   // if (isActive) {
                        count++
                        // сама вызовет исключение
                        ensureActive()
                    }
                        // корутина отменена, не ломат работу программы
                        throw CancellationException()
                    }
            Log.d("Main", "Finished: ${System.currentTimeMillis() - before}")
        }
        //слушатель, когда завершится
        job.invokeOnCompletion {
            Log.d("Main", "Finished")

        }
        viewModelScope.launch {
            delay(3000)
            job.cancel()
        }
    }

    private fun error() {
        throw RuntimeException()
    }


    override fun onCleared() {
        super.onCleared()
        coroutineScope.cancel()
    }

    companion object {
        private const val CR_ERROR = "ERROR"
    }
}