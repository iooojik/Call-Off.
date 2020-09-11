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

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Toast.makeText(this, "fnjsknfds", Toast.LENGTH_LONG).show()
        mTelephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val c = Class.forName(mTelephonyManager.javaClass.name)
        val m = c.getDeclaredMethod("getITelephony")
        m.isAccessible = true
        val telephonyService = m.invoke(mTelephonyManager) as ITelephony
        val bundle = intent!!.extras
        val phoneNumber = bundle!!.getString("incoming_number")
        phoneNumber.toString().replace("[^0-9]", "")
        Log.e("INCOMING", phoneNumber.toString())
        if ((phoneNumber != null)) {
            telephonyService.endCall()
            Log.d("HANG UP", phoneNumber)
        }

        return super.onStartCommand(intent, flags, startId)
    }
}