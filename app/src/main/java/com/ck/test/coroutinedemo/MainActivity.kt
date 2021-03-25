package com.ck.test.coroutinedemo

import android.os.Bundle
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main

class MainActivity : AppCompatActivity() {

    private val PROGRESS_MAX = 100
    private val PROGRESS_START = 0
    private val JOB_TIME = 4000 //ms
    private lateinit var job: CompletableJob  // complete the job by own

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        job_button.setOnClickListener {
            if (!::job.isInitialized) {
                initJob()
            }
            job_progress_bar.startJobOrCancel(job)
        }

        bt_next.setOnClickListener {
            this@MainActivity.startClearTopActivity(ParentCoroutineActivity::class.java)
        }
        bt_async_await.setOnClickListener {
            this@MainActivity.startClearTopActivity(AysncNAwaitActivity::class.java)
        }
    }

    fun ProgressBar.startJobOrCancel(job: Job) {
        if (this.progress > 0) {
            print("${job} is already active. Cancelling...")
            resetJob()
        } else {
            job_button.text = "Cancel Job #1"
            CoroutineScope(IO + job).launch {
                //background thread
                print("coroutine ${this} is activiated with job ${job}")
                for (i in PROGRESS_START..PROGRESS_MAX) {
                    delay((JOB_TIME / PROGRESS_MAX).toLong())
                    this@startJobOrCancel.progress = i
                }

                updatedJobCompleteText("job is completed")
            }
        }
    }

    fun updatedJobCompleteText(text: String) {
        GlobalScope.launch(Main) {
            job_complete_text.text = text
        }
    }

    private fun resetJob() {
        if (job.isActive || job.complete()){
            job.cancel(CancellationException("Resetting job"))
        }
        initJob()
    }

    fun initJob() {
        job_button.text = "start Job #1"
        updatedJobCompleteText("")
        job = Job() // init Job object

        job.invokeOnCompletion {
            // whether job is complete or fail it will run this script
            it?.message.let {
                var msg = it
                if (msg.isNullOrBlank()) {
                    msg = "unknown cancellation error."
                }
                print("${job} was canceled. Reason: $msg")
                showToast(msg)
            }
        }

        job_progress_bar.max = PROGRESS_MAX
        job_progress_bar.progress = PROGRESS_START
    }

    fun showToast(msg: String) {
        GlobalScope.launch(Main) {
            Toast.makeText(this@MainActivity, msg, Toast.LENGTH_LONG).show()
        }
    }
}