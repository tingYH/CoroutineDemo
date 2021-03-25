package com.ck.test.coroutinedemo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_aysnc_n_await.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlin.system.measureTimeMillis

class AysncNAwaitActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_aysnc_n_await)
        button.setOnClickListener {
            setNewText("Click!")

            CoroutineScope(IO).launch {
                fakeApiRequest()
            }
        }
        bt_await.setOnClickListener {
            awaitDemo()
        }

        bt_async.setOnClickListener {
            asyncDemo()
        }
    }

    private fun setNewText(input: String) {
        val newText = text.text.toString() + "\n$input"
        text.text = newText
    }

    private suspend fun setTextOnMainThread(input: String) {
        withContext(Main) {
            setNewText(input)
        }
    }

    private suspend fun fakeApiRequest() {
        logThread("fakeApiRequest")

        val result1 = getResult1FromApi() // wait until job is done

        if (result1.equals("Result #1")) {

            setTextOnMainThread("Got $result1")

            val result2 = getResult2FromApi() // wait until job is done

            if (result2.equals("Result #2")) {
                setTextOnMainThread("Got $result2")
            } else {
                setTextOnMainThread("Couldn't get Result #2")
            }
        } else {
            setTextOnMainThread("Couldn't get Result #1")
        }
    }

    private fun awaitDemo() {
        setNewText("awaitDemo Click!")
        CoroutineScope(IO).launch {
            val executionTime = measureTimeMillis {
                val result1: Deferred<String> = async {
                    println("deubg launch job1: ${Thread.currentThread().name}")
                    getResult1FromApi()
                }
                val result2: Deferred<String> = async {
                    println("deubg launch job2: ${Thread.currentThread().name}")
                    getResult2FromApi()
                }
                setTextOnMainThread("GOT ${result1.await()}")
                setTextOnMainThread("GOT ${result2.await()}")
            }
            println("dubug : total time elapsed: $executionTime")
        }
    }

    private fun asyncDemo() {
        setNewText("asyncDemo Click!")
        CoroutineScope(IO).launch {
            val job1 = launch {
                val time1 = measureTimeMillis {
                    println("debug: launching job1 in thread: ${Thread.currentThread().name}")
                    val result1 = getResult1FromApi()
                    setTextOnMainThread("GOT $result1")
                }

                println("debug: completed job1 in $time1 ms")

            }
            val job2 = launch {
                val time2 = measureTimeMillis {
                    println("debug: launching job2 in thread: ${Thread.currentThread().name}")
                    val result2 = getResult2FromApi()
                    setTextOnMainThread("GOT $result2")
                }

                println("debug: completed job1 in $time2 ms")
            }
        }
    }

    private suspend fun getResult1FromApi(): String {
        logThread("getResult1FromApi")
        delay(1000) // Does not block thread. Just suspends the coroutine inside the thread
        return "Result #1"
    }

    private suspend fun getResult2FromApi(): String {
        logThread("getResult2FromApi")
        delay(1700)
        return "Result #2"
    }

    private fun logThread(methodName: String) {
        println("debug: ${methodName}: ${Thread.currentThread().name}")
    }
}