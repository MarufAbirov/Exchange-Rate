package tj.app.kursvalyuta

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNav: BottomNavigationView
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bottomNav = findViewById(R.id.bottom_nav)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.nav_favorable -> {
                    navController.navigate(R.id.favorableRateFragment)
                    it.isChecked = true
                    true
                }
                R.id.nav_nbt -> {
                    navController.navigate(R.id.nbtBankFragment)
                    it.isChecked = true
                    true
                }
                else -> false
            }
        }

    }

    override fun onBackPressed() {
        val navController = findNavController(R.id.nav_host_fragment)
        when (navController.currentDestination?.id) {
            R.id.nav_favorable -> {
                finishAffinity()
            }
            else -> {
                finishAffinity()
            }
        }
    }
}