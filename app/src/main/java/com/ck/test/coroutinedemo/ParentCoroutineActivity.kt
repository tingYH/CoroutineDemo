package com.ck.test.coroutinedemo

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_parent_coroutine.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main

class ParentCoroutineActivity : AppCompatActivity() {

    private val TAG = "DEBUG"

    private lateinit var parent: Job

    private val JOB_TIME = 2000 //ms

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_parent_coroutine)
        main()
        bt_stop_coroutine.setOnClickListener {
            parent.cancel()
        }
    }

    suspend fun work(i: Int) {
        delay(JOB_TIME.toLong())
        println("Work $i done. ${Thread.currentThread().name}")
    }

    //Coroutine Default設定: 若parent被取消則裡面的子job都會被刪除
    private fun main() {
        val startTime = System.currentTimeMillis()
        println("Starting Parent job...")
        parent = CoroutineScope(Main).launch {
            launch {
                work(1)
            }
            launch {
                work(2)
            }
        }

        parent.invokeOnCompletion { throwable ->
            if (throwable != null) {
                println("job was canceled after ${System.currentTimeMillis() - startTime} ms.")
            } else {
                println("Done in ${System.currentTimeMillis() - startTime} ms.")
            }
        }
    }

    //使用GlobalScope 則parent被刪除後 coroutine一樣會繼續執行
//    private fun main() {
//        val startTime = System.currentTimeMillis()
//        println("Starting Parent job...")
//        parent = CoroutineScope(Main).launch {
//            GlobalScope.launch {
//                work(1)
//            }
//            GlobalScope.launch {
//                work(2)
//            }
//        }
//
//        parent.invokeOnCompletion { throwable ->
//            if (throwable != null) {
//                println("job was canceled after ${System.currentTimeMillis() - startTime} ms.")
//            } else {
//                println("Done in ${System.currentTimeMillis() - startTime} ms.")
//            }
//        }
//    }

    private fun println(msg: String) {
        Log.d(TAG, msg)
    }
}