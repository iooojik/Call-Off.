package iooojik.ru.calloff.services

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.PowerManager
import android.telecom.InCallService
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import iooojik.ru.calloff.MainActivity
import iooojik.ru.calloff.R
import iooojik.ru.calloff.StaticVars


class CallControlService : InCallService() {

    private lateinit var notificationManager: NotificationManager
    private lateinit var powerManager: PowerManager
    private lateinit var wakeLock: PowerManager.WakeLock

    override fun onCreate() {
        super.onCreate()
        //получаем notificationManager для отображения уведомления
        notificationManager = this.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        powerManager = getSystemService(POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Call Off.:tag")
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        wakeLock.acquire(10*60*1000L /*10 minutes*/)
        //показываем уведомление о работе приложения в фоновом процессе
        sendNotification("Запущен фоновый процесс", "Номера из \"Белого списка\" могут вам звонить.")
        //какой-то процесс
        doTask()
        return START_STICKY
    }

    private fun doTask(){
        //что-то делаем

        val mainHandler = Handler()
        mainHandler.post(object : Runnable {
            override fun run() {
                Log.i("CALL OFF. INFO", "SERVICE IS RUNNING")
                sendNotification("Запущен фоновый процесс", "Номера из \"Белого списка\" могут вам звонить.")
                mainHandler.postDelayed(this, 600000)
            }
        })
    }

    private fun sendNotification(title: String, text: String){
        //показываем уведомление

        //активити, которое будет работать в фоне
        val notificationIntent = Intent(this, MainActivity::class.java)
        notificationIntent.action = Intent.ACTION_MAIN
        notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER)

        //PendingIntent (что будем показывать при нажатии на уведоление)
        val contentIntent = PendingIntent.getActivity(
            applicationContext,
            0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT
        )
        //если версия SDK > O , то создаём канал для получения уведомлений
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(StaticVars().NOTIFICATION_NAME.toString(), StaticVars().NOTIFICATION_NAME);
        }
        //как только канал создан, показываем уведомление

        val builder = NotificationCompat
            .Builder(this, StaticVars().NOTIFICATION_NAME)
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setContentIntent(contentIntent)//что открываем при нажатии на уведомление
            .setOngoing(true)//убираем возможность удаления уведомления
            .setContentTitle(title) // название
            .setContentText(text) // текст
            .setAutoCancel(false)
            .setPriority(NotificationCompat.PRIORITY_HIGH) // приоритет

        //создаём уведомление
        val notificationManager = NotificationManagerCompat.from(this)
        notificationManager.notify(StaticVars().NOTIFICATION_ID, builder.build())
        val notification : Notification
        notification = builder.build()
        //запускаем фоновый процесс
        startForeground(StaticVars().NOTIFICATION_ID, notification)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(channelId: String, channelName: String): String? {
        val chan = NotificationChannel(
            channelId,
            channelName, NotificationManager.IMPORTANCE_NONE
        )
        chan.lightColor = Color.BLUE
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(chan)
        return channelId
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        wakeLock.release()
        //удаляем напоминание при завершении работы сервиса
        notificationManager.cancel(StaticVars().NOTIFICATION_ID)
        //останавливаем сервис
        //stopSelf()
    }

}