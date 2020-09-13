package iooojik.ru.calloff.receivers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import iooojik.ru.calloff.R
import iooojik.ru.calloff.StaticVars

class NotificationReceiver : BroadcastReceiver() {
    //Receiver для отображения напоминаний

    override fun onReceive(context: Context, intent: Intent) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        //получаем данные из bundle от фрагмента/активности
        val args = intent.extras

        //устанавливаем напоминание
        val builder = NotificationCompat.Builder(context, StaticVars().NOTIFICATION_NAME)
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setContentTitle("model.getName()")
            .setContentText("model.getInfo()")
            .setShowWhen(true)
            .setAutoCancel(false)
            .setPriority(NotificationCompat.PRIORITY_MAX)
        createChannelIfNeeded(notificationManager)
        notificationManager.notify(StaticVars().NOTIFICATION_ID, builder.build())
    }

    private fun createChannelIfNeeded(manager: NotificationManager?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel =
                NotificationChannel(
                    StaticVars().NOTIFICATION_ID.toString(),
                    StaticVars().NOTIFICATION_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT
                )
            manager!!.createNotificationChannel(notificationChannel)
        }
    }
}