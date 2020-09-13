package iooojik.ru.calloff

import android.Manifest

class StaticVars {
    val preferencesName = "MAIN PREFERENCES"
    val callsController = "CALLS CONTROLLER"
    val myContactsInWhiteList = "MY CONTACTS IN WHITE LIST STATE"
    val NOTIFICATION_NAME = "Напоминание"
    val NOTIFICATION_ID = 102

        /* список запрашиваемых разрешений
         * 1. выполнение вызовов и управление ими
         * 2. доступ к списку вызовов
         * 3. доступ к списку контактов(и управление контактами)
         */
    val perms = arrayOf(
        Manifest.permission.READ_PHONE_STATE, Manifest.permission.CALL_PHONE,
        Manifest.permission.READ_CALL_LOG, Manifest.permission.READ_CONTACTS,
        Manifest.permission.WRITE_CONTACTS)

    val TOAST_WARNING_MESSAGE = "Что-то пошло не так"
}