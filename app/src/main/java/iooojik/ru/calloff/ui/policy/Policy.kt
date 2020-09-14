package iooojik.ru.calloff.ui.policy

import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Html
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import com.google.android.material.snackbar.Snackbar
import iooojik.ru.calloff.R
import iooojik.ru.calloff.StaticVars

class Policy : Fragment(), View.OnClickListener {

    private lateinit var rootView : View
    private lateinit var preferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_policy, container, false)
        preferences = requireActivity().getSharedPreferences(StaticVars().preferencesName, Context.MODE_PRIVATE)
        initialize()
        return rootView
    }

    private fun initialize(){
        val policyTermsText = rootView.findViewById<TextView>(R.id.policy_text)
        policyTermsText.setText(R.string.policy_html)
        val cancelButton = rootView.findViewById<Button>(R.id.cancel_policy)
        val acceptPolicy = rootView.findViewById<Button>(R.id.accept_policy)
        cancelButton.setOnClickListener(this)
        acceptPolicy.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.cancel_policy -> {
                Snackbar.make(requireView(), "К сожалению, вы не сможете пользоваться приложением, " +
                        "пока не примете условия пользовательского соглашения.", Snackbar.LENGTH_LONG).show()
            }
            R.id.accept_policy -> {
                if (!requestPermissions()){
                    Snackbar.make(requireView(), "Пожалуйста, нажмите \"Принять\" ещё раз, чтобы продолжить.", Snackbar.LENGTH_LONG).show()
                } else{
                    preferences.edit().putInt(StaticVars().policyChecked, 1).apply()
                    requireActivity().findNavController(R.id.nav_host_fragment).navigate(R.id.nav_home)
                }
            }
        }
    }

    private fun requestPermissions() : Boolean{
        //проверяем наличие разрешений

        val perms = StaticVars().perms
        var permissionStatus = PackageManager.PERMISSION_GRANTED

        for (perm in perms) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(), perm) == PackageManager.PERMISSION_DENIED) {
                permissionStatus = PackageManager.PERMISSION_DENIED
                break
            }
        }

        //ещё раз проверяем наличие разрешений
        if (permissionStatus != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(requireActivity(), perms, 1)

        return permissionStatus == PackageManager.PERMISSION_GRANTED

    }

}