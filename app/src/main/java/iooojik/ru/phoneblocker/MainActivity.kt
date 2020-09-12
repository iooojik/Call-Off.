package iooojik.ru.phoneblocker

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.telecom.TelecomManager
import android.widget.Toast
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
        /*
        //запрос на использование данного приложения в качестве дефолтного для управления вызовами
        if (getSystemService(TelecomManager::class.java).defaultDialerPackage != packageName) {
            Intent(TelecomManager.ACTION_CHANGE_DEFAULT_DIALER)
                .putExtra(TelecomManager.EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME, packageName)
                .let(::startActivity)
        }
         */
        //получаем SharedPreferences
        preferences = this.getSharedPreferences(StaticVars().preferencesName, Context.MODE_PRIVATE)
        //запрашиваем разрешения
        //requestPermissions()
        //настраиваем навигацию
        navigationSetup()
    }

    private fun requestPermissions(){
        //проверяем наличие разрешений

        val perms = StaticVars().perms
        var permissionStatus = PackageManager.PERMISSION_GRANTED

        for (perm in perms) {
            if (ContextCompat.checkSelfPermission(
                    applicationContext, perm) == PackageManager.PERMISSION_DENIED) {
                permissionStatus = PackageManager.PERMISSION_DENIED
                break
            }
        }

        //ещё раз проверяем наличие разрешений
        if (permissionStatus != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, perms, 1)

    }

    private fun navigationSetup(){
        //контроллер навигации
        navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        //views
        val drawer : DrawerLayout = findViewById(R.id.drawer)
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_nav_view)
        AppBarConfiguration.Builder(R.id.nav_home).setDrawerLayout(drawer).build()
        NavigationUI.setupWithNavController(bottomNavigationView, navController)

        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.show()

        val cornerSize = resources.getDimension(R.dimen.large_components_dimen)
        val customButtonShapeBuilder = ShapeAppearanceModel.Builder()
        customButtonShapeBuilder.setTopLeftCorner(CornerFamily.CUT, cornerSize)
        customButtonShapeBuilder.setTopRightCorner(CornerFamily.CUT, cornerSize)
        val materialShapeDrawable = MaterialShapeDrawable(customButtonShapeBuilder.build())
        materialShapeDrawable.fillColor = ContextCompat.getColorStateList(applicationContext, R.color.colorAccent)

        bottomNavigationView.background = materialShapeDrawable

        //переходим на начальную страницу
        findNavController(R.id.nav_host_fragment).navigate(R.id.nav_home)

    }
}