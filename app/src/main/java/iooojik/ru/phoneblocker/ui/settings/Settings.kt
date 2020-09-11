package iooojik.ru.phoneblocker.ui.settings

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat.getSystemService
import com.google.android.material.floatingactionbutton.FloatingActionButton
import iooojik.ru.phoneblocker.CallControlService
import iooojik.ru.phoneblocker.R
import iooojik.ru.phoneblocker.StaticVars
import kotlinx.android.synthetic.main.fragment_settings.*

class Settings : Fragment() {
    private lateinit var rootView: View
    private lateinit var preferences: SharedPreferences

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
        requireActivity().findViewById<FloatingActionButton>(R.id.fab).hide()
        val callsController = rootView.findViewById<SwitchCompat>(R.id.calls_controller)
        preferences = requireActivity().getSharedPreferences(StaticVars().preferencesName, Context.MODE_PRIVATE)
        if (preferences.getInt(StaticVars().callsController, 0) == 1)
            callsController.isChecked = true

        callsController.setOnCheckedChangeListener{buttonView, isChecked ->
            val callControlService = CallControlService::class.java
            val intent = Intent(requireContext(), callControlService)

            if (isChecked) {
                preferences.edit().putInt(StaticVars().callsController, 1).apply()
                requireActivity().startService(intent)
            }
            else {
                preferences.edit().putInt(StaticVars().callsController, 0).apply()
                if (isServiceRunning(callControlService)){
                    requireActivity().stopService(intent)
                }
            }
        }

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