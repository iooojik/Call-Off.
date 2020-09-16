package iooojik.ru.calloff.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.telephony.TelephonyManager
import com.android.internal.telephony.ITelephony
import iooojik.ru.calloff.StaticVars
import iooojik.ru.calloff.localData.AppDatabase
import iooojik.ru.calloff.localData.whiteList.WhiteListDao


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
                if (model.firstPhoneNumber == phoneNumber || model.secondPhoneNumber == phoneNumber) {
                    isReject = false
                    break
                }
            }
            if (isReject) {
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
            }else return
        } else return
    }

}