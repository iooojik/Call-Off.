package iooojik.ru.calloff

import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
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
        if (preferences.getInt(StaticVars().policyChecked, 0) == 1)
            findNavController(R.id.nav_host_fragment).navigate(R.id.nav_home)
        else {
            findViewById<FloatingActionButton>(R.id.fab).hide()
            bottomNavigationView.visibility = View.GONE
            findNavController(R.id.nav_host_fragment).navigate(R.id.nav_policy)
        }

    }
}