package iooojik.ru.calloff.receivers

import android.content.*
import android.telephony.TelephonyManager
import android.util.Log
import com.android.internal.telephony.ITelephony
import iooojik.ru.calloff.StaticVars
import iooojik.ru.calloff.localData.AppDatabase
import iooojik.ru.calloff.localData.whiteList.WhiteListDao
import iooojik.ru.calloff.services.CallControlService


class PhoneCallStateReceiver : BroadcastReceiver() {
    private lateinit var mTelephonyManager: TelephonyManager
    private lateinit var preferences: SharedPreferences
    private lateinit var database: AppDatabase
    private lateinit var whiteListDao: WhiteListDao



    override fun onReceive(context: Context?, intent: Intent?) {
        //проверяем должны ли мы отклонять вызов
        preferences = context!!.getSharedPreferences(StaticVars().preferencesName, Context.MODE_PRIVATE)
        if (preferences.getInt(StaticVars().callsController, 0) == 1) {
            //получаем номер телефона(входящего и исходящего)
            val bundle = intent!!.extras
            val phoneNumber = bundle!!.getString(TelephonyManager.EXTRA_INCOMING_NUMBER)
            phoneNumber.toString().replace("[^0-9]", "")

            database = AppDatabase.getAppDataBase(context)!!
            whiteListDao = database.whiteListDao()
            val whiteList = whiteListDao.getAll()
            var isReject = true
            for (model in whiteList){
                val myCurrentContactList = model.phoneNumbers.split(StaticVars().regex)
                for (num in myCurrentContactList){
                    if (phoneNumber == num){
                        isReject = false
                        break
                    }
                }
            }
            if (isReject && intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                //отклоняем входящие вызовы
                //получаем mTelephonyManager и telephonyService для управления звонком
                mTelephonyManager =
                    context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                val c = Class.forName(mTelephonyManager.javaClass.name)
                val m = c.getDeclaredMethod("getITelephony")
                m.isAccessible = true
                val telephonyService = m.invoke(mTelephonyManager) as ITelephony
                //если звонок не исходящий (phoneNumber != null) и номер не находится в чёрном списке,
                //то отклоняем звонок, иначе телефон продолжает звонить
                if (phoneNumber != null) {
                    telephonyService.silenceRinger()
                    telephonyService.endCall()
                }
                val intentService = Intent(context, CallControlService::class.java)
                context.startService(intentService)
            }
        }

    }



}