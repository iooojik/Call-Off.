package iooojik.ru.calloff.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import iooojik.ru.calloff.services.CallControlService

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {
        val intentService = Intent(p0, CallControlService::class.java)
        p0!!.startService(intentService)
    }
}