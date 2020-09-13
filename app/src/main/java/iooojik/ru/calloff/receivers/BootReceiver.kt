package iooojik.ru.calloff.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import iooojik.ru.calloff.services.CallControlService

class BootReceiver : BroadcastReceiver() {
    //Receiver для автоматического старта сервиса после перезагрузки
    override fun onReceive(context: Context?, intent: Intent?) {
        val serviceIntent = Intent(context, CallControlService::class.java)
        context!!.startService(serviceIntent)
    }

}