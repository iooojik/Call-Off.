package iooojik.ru.calloff

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.telecom.Call
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel
import iooojik.ru.calloff.receivers.PhoneCallStateReceiver
import iooojik.ru.calloff.services.CallControlService


@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var preferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initialization()
    }

    private fun initialization(){
        //получаем SharedPreferences
        preferences = this.getSharedPreferences(StaticVars().preferencesName, Context.MODE_PRIVATE)
        //настраиваем навигацию
        navigationSetup()
        val receiver = PhoneCallStateReceiver()
        val intFilter = IntentFilter("iooojik.ru.calloff")
        registerReceiver(receiver, intFilter)
    }

    private fun navigationSetup(){
        //контроллер навигации
        navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        //views
        val drawer : DrawerLayout = findViewById(R.id.drawer)
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_nav_view)
        AppBarConfiguration.Builder(R.id.nav_white_list).setDrawerLayout(drawer).build()
        NavigationUI.setupWithNavController(bottomNavigationView, navController)

        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.show()

        if (preferences.getInt(StaticVars().callsController, 0) == 1){
            val intent = Intent(applicationContext, CallControlService().javaClass)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                ContextCompat.startForegroundService(this, intent)
            }else ContextCompat.startForegroundService(this, intent)
        }

        //переходим на начальную страницу
        if (preferences.getInt(StaticVars().policyChecked, 0) == 1) {
            //проверяем наличие разрешений
            val perms: Array<String> = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
                StaticVars().permsAPI28
            else StaticVars().permsAPI23
            var permissionStatus = PackageManager.PERMISSION_GRANTED
            for (perm in perms) {
                if (ContextCompat.checkSelfPermission(applicationContext, perm) == PackageManager.PERMISSION_DENIED) {
                    permissionStatus = PackageManager.PERMISSION_DENIED
                    break
                }
            }

            if (permissionStatus == PackageManager.PERMISSION_DENIED){
                findViewById<FloatingActionButton>(R.id.fab).hide()
                bottomNavigationView.visibility = View.GONE
                preferences.edit().putInt(StaticVars().policyChecked, 0).apply()
                findNavController(R.id.nav_host_fragment).navigate(R.id.nav_white_list)
            }
        }
        else {
            findViewById<FloatingActionButton>(R.id.fab).hide()
            bottomNavigationView.visibility = View.GONE
            findNavController(R.id.nav_host_fragment).navigate(R.id.nav_policy)
        }

    }
}