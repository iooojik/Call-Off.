package iooojik.ru.calloff.ui.settings

import android.app.ActivityManager
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.appcompat.widget.SwitchCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import iooojik.ru.calloff.services.CallControlService
import iooojik.ru.calloff.R
import iooojik.ru.calloff.StaticVars
import iooojik.ru.calloff.localData.AppDatabase
import iooojik.ru.calloff.localData.whiteList.WhiteListDao
import iooojik.ru.calloff.localData.whiteList.WhiteListModel

class Settings : Fragment() {
    private lateinit var rootView: View
    private lateinit var preferences: SharedPreferences
    private lateinit var database: AppDatabase
    private lateinit var whiteListDao: WhiteListDao

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_settings, container, false)
        return rootView
    }

    override fun onStart() {
        super.onStart()
        initialize()
    }

    private fun initialize(){
        database = AppDatabase.getAppDataBase(requireContext())!!
        whiteListDao = database.whiteListDao()
        requireActivity().findViewById<FloatingActionButton>(R.id.fab).hide()
        val callsController = rootView.findViewById<SwitchCompat>(R.id.calls_controller)
        preferences = requireActivity().getSharedPreferences(StaticVars().preferencesName, Context.MODE_PRIVATE)
        if (preferences.getInt(StaticVars().callsController, 0) == 1)
            callsController.isChecked = true

        callsController.setOnCheckedChangeListener{buttonView, isChecked ->
            val callControlService = CallControlService::class.java
            val intent = Intent(requireContext(), callControlService)

            if (isChecked) {
                requireActivity().startService(intent)
                preferences.edit().putInt(StaticVars().callsController, 1).apply()
                Snackbar.make(requireView(), "Включено", Snackbar.LENGTH_SHORT).show()

            }
            else {
                if (isServiceRunning(callControlService)){
                    requireActivity().stopService(intent)
                }
                preferences.edit().putInt(StaticVars().callsController, 0).apply()
                Snackbar.make(requireView(), "Выключено", Snackbar.LENGTH_SHORT).show()

            }
        }

        val addContactsToWhiteList = rootView.findViewById<CheckBox>(R.id.myContactsInWhiteList)
        if (preferences.getInt(StaticVars().myContactsInWhiteList, 0) == 1){
            addContactsToWhiteList.isChecked = true
        }
        addContactsToWhiteList.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked){
                preferences.edit().putInt(StaticVars().myContactsInWhiteList, 1).apply()
                val myContacts = getContactList()
                for (model in myContacts){
                    model.isMyContact = true
                    whiteListDao.insert(model)
                }
                Snackbar.make(requireView(), "Добавлено", Snackbar.LENGTH_SHORT).show()

            } else {
                preferences.edit().putInt(StaticVars().myContactsInWhiteList, 0).apply()
                val whiteList = whiteListDao.getMyContacts(true)
                for (model in whiteList)
                    whiteListDao.delete(model)

                Snackbar.make(requireView(), "Удалено", Snackbar.LENGTH_SHORT).show()

            }
        }

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

                        if (index == 1 && phoneNo.toCharArray()[0] == '+')
                            model.firstPhoneNumber = phoneNo
                        else if(index == 2 && phoneNo.toCharArray()[0] == '+')
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

    private fun isServiceRunning(serviceClass: Class<*>): Boolean {
        val activityManager = requireActivity().getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

        // Loop through the running services
        for (service in activityManager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                // If the service is running then return true
                return true
            }
        }
        return false
    }
}