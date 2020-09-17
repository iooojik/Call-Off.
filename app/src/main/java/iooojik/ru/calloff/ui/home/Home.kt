package iooojik.ru.calloff.ui.home

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ContentResolver
import android.content.Context
import android.content.SharedPreferences
import android.database.Cursor
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.provider.CallLog
import android.provider.ContactsContract
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import iooojik.ru.calloff.R
import iooojik.ru.calloff.StaticVars
import iooojik.ru.calloff.localData.AppDatabase
import iooojik.ru.calloff.localData.callLog.CallLogDao
import iooojik.ru.calloff.localData.callLog.CallLogModel
import iooojik.ru.calloff.localData.whiteList.WhiteListModel
import java.lang.Exception
import java.lang.Long
import java.util.*
import kotlin.collections.ArrayList


class Home : Fragment() {
    private lateinit var rootView : View
    private lateinit var callLogs : MutableList<CallLogModel>
    private lateinit var inflater: LayoutInflater
    private lateinit var myContacts : MutableList<WhiteListModel>
    private lateinit var callLogDao: CallLogDao
    private lateinit var database : AppDatabase
    private lateinit var preferences: SharedPreferences


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_home, container, false)
        requireActivity().findViewById<FloatingActionButton>(R.id.fab).hide()
        database = AppDatabase.getAppDataBase(requireContext())!!
        callLogDao = database.callLogDao()
        Thread {
            try {
                initialize()
            } catch (e : Exception){
                requireActivity().runOnUiThread {
                    Log.e("error", e.toString())
                    Toast.makeText(requireContext(), StaticVars().TOAST_WARNING_MESSAGE, Toast.LENGTH_SHORT).show()
                }
            }
        }.start()
        return rootView
    }

    private fun initialize(){
        inflater = requireActivity().layoutInflater
        myContacts = getContactList()
        callLogs = getCallLogs()
        callLogs.reverse()
        preferences = requireActivity().getSharedPreferences(StaticVars().preferencesName, Context.MODE_PRIVATE)


        requireActivity().runOnUiThread {

            val bottomNavigationView = requireActivity().findViewById<BottomNavigationView>(R.id.bottom_nav_view)
            if (bottomNavigationView.visibility == View.GONE)
                bottomNavigationView.visibility = View.VISIBLE

            val recView = rootView.findViewById<RecyclerView>(R.id.rec_view_call_log)
            recView.layoutManager = LinearLayoutManager(context)
            recView.adapter = CallLogAdapter(requireContext(), inflater, callLogs, requireActivity())
        }

        //если версия SDK > O , то создаём канал для получения уведомлений
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && preferences.getInt(StaticVars().notificationCreated, 0) == 0) {
            createNotificationChannel(StaticVars().NOTIFICATION_NAME, StaticVars().NOTIFICATION_NAME)
            preferences.edit().putInt(StaticVars().notificationCreated, 1).apply()
        }
    }

    @SuppressLint("MissingPermission")
    private fun getCallLogs() : MutableList<CallLogModel>{
        // получение списка вызовов
        val models : MutableList<CallLogModel> = mutableListOf()
        val calllogsBuffer = ArrayList<String>()
        calllogsBuffer.clear()
        val managedCursor: Cursor = requireActivity().contentResolver.query(
            CallLog.Calls.CONTENT_URI,
            null, null, null, null
        )!!
        val number: Int = managedCursor.getColumnIndex(CallLog.Calls.NUMBER)
        val type: Int = managedCursor.getColumnIndex(CallLog.Calls.TYPE)
        val date: Int = managedCursor.getColumnIndex(CallLog.Calls.DATE)

        val duration: Int = managedCursor.getColumnIndex(CallLog.Calls.DURATION)
        while (managedCursor.moveToNext()) {
            val phNumber: String = managedCursor.getString(number)
            val callType: String = managedCursor.getString(type)
            val callDate: String = managedCursor.getString(date)
            val callDayTime = Date(Long.valueOf(callDate))
            val callDuration: String = managedCursor.getString(duration)
            var dir: String? = null
            val dircode = callType.toInt()
            when (dircode) {
                CallLog.Calls.OUTGOING_TYPE -> dir = "OUTGOING"
                CallLog.Calls.INCOMING_TYPE -> dir = "INCOMING"
                CallLog.Calls.MISSED_TYPE -> dir = "MISSED"
            }


            calllogsBuffer.add(
                """
                    Phone Number: $phNumber 
                    Call Type: $dir 
                    Call Date: $callDayTime 
                    Call duration in sec : $callDuration
                """
            )
            val model = CallLogModel(null, getString(R.string.unknown_caller),
                phNumber.toString(),null.toString(),false, callDayTime.toString(), dir.toString())

            for (md in myContacts){
                if (phNumber == md.firstPhoneNumber || phNumber == md.secondPhoneNumber){
                    model.firstPhoneNumber = md.firstPhoneNumber
                    if (md.secondPhoneNumber != "null" && md.secondPhoneNumber != md.firstPhoneNumber)
                        model.secondPhoneNumber = md.secondPhoneNumber
                    model.name = md.name
                    model.isMyContact = true
                    break
                }
            }

            models.add(model)
        }
        if (models.size != callLogDao.getAll().size){
            callLogDao.deleteAll()
            for (md in models)
                callLogDao.insert(md)
        }
        managedCursor.close()
        requireActivity().runOnUiThread {
            val pgBar = rootView.findViewById<ProgressBar>(R.id.progressBar)
            if (pgBar != null)
                pgBar.visibility = View.GONE
        }
        return models
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(channelId: String, channelName: String): String? {
        val chan = NotificationChannel(
            channelId,
            channelName, NotificationManager.IMPORTANCE_NONE
        )
        chan.lightColor = Color.BLUE
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val manager = requireActivity().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(chan)
        return channelId
    }

    private fun getContactList() : MutableList<WhiteListModel> {
        val models : MutableList<WhiteListModel> = mutableListOf()
        //получение контактов
        val cr: ContentResolver = requireActivity().contentResolver
        val cur = cr.query(
            ContactsContract.Contacts.CONTENT_URI,
            null, null, null, null
        )
        if (cur?.count ?: 0 > 0) {
            while (cur != null && cur.moveToNext()) {
                val id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID))

                val name = cur.getString(
                    cur.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME
                    )
                )
                if (cur.getInt(
                        cur.getColumnIndex(
                            ContactsContract.Contacts.HAS_PHONE_NUMBER
                        )
                    ) > 0
                ) {
                    val pCur = cr.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                        arrayOf(id),
                        null
                    )
                    var index = 1
                    val model = WhiteListModel(null, null.toString(), null.toString(), null.toString(), true)
                    while(pCur!!.moveToNext()) {
                        var phoneNo = pCur.getString(
                            pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER
                            )
                        )
                        phoneNo = phoneNo.replace(" ", "", false)
                        phoneNo = phoneNo.replace("-", "", false)
                        Log.i("TAG", "Name: $name")
                        Log.i("TAG", "Phone Number: $phoneNo")
                        if (name != null)
                            model.name = name

                        if (index == 1)
                            model.firstPhoneNumber = phoneNo
                        else if(index == 2)
                            model.secondPhoneNumber = phoneNo
                        index++
                    }
                    models.add(model)
                    pCur.close()
                }
            }
        }
        cur?.close()
        return models
    }
}