package iooojik.ru.phoneblocker

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.SystemClock
import android.telecom.Call
import android.telecom.InCallService
import android.telephony.TelephonyManager
import android.util.Log
import android.widget.Toast
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.android.internal.telephony.ITelephony
import com.google.android.material.appbar.AppBarLayout


class CallControlService : InCallService() {

    private lateinit var mHandler: Handler
    private lateinit var mRunnable: Runnable
    private lateinit var mTelephonyManager: TelephonyManager
    var mKeepAlivePendingIntent: PendingIntent? = null

    private var i : Int = 0

    @SuppressLint("ShortAlarm")
    override fun onCreate() {
        super.onCreate()

    }

}