package com.ck.test.coroutinedemo

import android.app.Activity
import android.content.Intent

/**
 *
 * @author ChengKai YH
 * @version $
 * <p/>
 * <p/> $
 */
fun <A : Activity> Activity.startClearTopActivity(activity: Class<A>) {
    Intent(this, activity).also {
        it.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(it)
    }
}
