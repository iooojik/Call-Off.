package iooojik.ru.phoneblocker

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
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


class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var preferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initialization()
    }

    private fun initialization(){
        preferences = this.getSharedPreferences(StaticVars().preferencesName, Context.MODE_PRIVATE)
        requestPermissions()
        navigationSetup()
    }

    private fun requestPermissions(){
        val perms = arrayOf(
            Manifest.permission.READ_PHONE_STATE, Manifest.permission.CALL_PHONE,
            Manifest.permission.READ_CALL_LOG, Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS)

        //проверяем наличие разрешения
        var permissionStatus = PackageManager.PERMISSION_GRANTED

        for (perm in perms) {
            if (ContextCompat.checkSelfPermission(
                    applicationContext, perm) == PackageManager.PERMISSION_DENIED) {
                permissionStatus = PackageManager.PERMISSION_DENIED
                break
            }
        }

        if (permissionStatus != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, perms, 1)

    }

    private fun navigationSetup(){
        navController = Navigation.findNavController(this, R.id.nav_host_fragment)
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
        findNavController(R.id.nav_host_fragment).navigate(R.id.nav_home)

    }


}

fun Context.toast(message : String){
    Toast.makeText(applicationContext, message,Toast.LENGTH_SHORT).show()
}