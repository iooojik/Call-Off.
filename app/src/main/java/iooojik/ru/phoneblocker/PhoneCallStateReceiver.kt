package iooojik.ru.phoneblocker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import android.util.Log
import com.android.internal.telephony.ITelephony


class PhoneCallStateReceiver : BroadcastReceiver() {
    private lateinit var mTelephonyManager: TelephonyManager

    override fun onReceive(context: Context?, intent: Intent?) {

        //сброс звонка
        /*
        mTelephonyManager = context!!.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
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
        */

    }

}