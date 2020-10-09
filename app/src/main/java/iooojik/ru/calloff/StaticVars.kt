package iooojik.ru.calloff

import android.Manifest
import android.os.Build
import androidx.annotation.RequiresApi

class StaticVars {
    //название настроек
    val preferencesName = "MAIN PREFERENCES"
    //логи
    val callsController = "CALLS CONTROLLER"
    val myContactsInWhiteList = "MY CONTACTS IN WHITE LIST STATE"
    val policyChecked = "USER CHECKED POLICY"
    val notificationCreated = "NOTIFICATION CREATED"
    //код и название уведомлений
    val NOTIFICATION_NAME = "Напоминание"
    val TIME_SERVICE_DIED ="TIME SERVICED DIED"
    val NOTIFICATION_ID = 102

        /* список запрашиваемых разрешений
         * 1. выполнение вызовов и управление ими
         * 2. доступ к списку вызовов
         * 3. доступ к списку контактов(и управление контактами)
         */
        @RequiresApi(Build.VERSION_CODES.P)
        val permsAPI28 = arrayOf(
        Manifest.permission.READ_PHONE_STATE, Manifest.permission.CALL_PHONE,
        Manifest.permission.READ_CALL_LOG, Manifest.permission.READ_CONTACTS,
        Manifest.permission.WRITE_CONTACTS, Manifest.permission.WAKE_LOCK,
            Manifest.permission.FOREGROUND_SERVICE)

        val permsAPI23 = arrayOf(
        Manifest.permission.READ_PHONE_STATE, Manifest.permission.CALL_PHONE,
        Manifest.permission.READ_CALL_LOG, Manifest.permission.READ_CONTACTS,
        Manifest.permission.WRITE_CONTACTS, Manifest.permission.WAKE_LOCK)
    //стандратное уведомление об ошибках
    val TOAST_WARNING_MESSAGE = "Что-то пошло не так"
    val regex = "***@iooojik@***"
}