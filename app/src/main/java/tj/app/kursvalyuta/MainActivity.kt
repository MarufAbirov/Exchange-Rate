package tj.app.kursvalyuta

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch
import tj.app.kursvalyuta.retrofit.RetrofitInstance

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var bottomNav: BottomNavigationView
    private var backCallback: OnBackPressedCallback? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNav = findViewById(R.id.bottom_nav)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        lifecycleScope.launch {
            try {
                val bank = RetrofitInstance.api.getBankList().find {
                    it.shortName.equals("humo", ignoreCase = true)
                }

                if (bank != null) {
                    val bundle = Bundle().apply {
                        putParcelable("humoBank", bank)
                    }
                    navController.setGraph(R.navigation.nav_graph, bundle)

                    setupBottomNav()
                    setupBackHandler()
                } else {
                    Toast.makeText(this@MainActivity, "Humo ёфт нашуд", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@MainActivity, "Хатогӣ: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setupBottomNav() {
        bottomNav.setOnItemSelectedListener { item ->
            if (item.itemId != navController.currentDestination?.id) {
                when (item.itemId) {
                    R.id.nav_favorable -> {
                        navController.navigate(R.id.favorableRateFragment)
                        lifecycleScope.launch {
                            val bank = RetrofitInstance.api.getBankList().find {
                                it.shortName.equals("humo", ignoreCase = true)
                            }

                            if (bank != null) {
                                val bundle = Bundle().apply {
                                    putParcelable("humoBank", bank)
                                }
                                navController.setGraph(R.navigation.nav_graph, bundle)
                            }
                        }

                            true
                    }

                    R.id.nav_nbt -> {
                        navController.navigate(R.id.nbtBankFragment)
                        true
                    }

                    else -> false
                }
            } else true
        }
    }

    private fun setupBackHandler() {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            bottomNav.visibility =
                if (destination.id == R.id.internalCoursePage) View.GONE
                else View.VISIBLE

            backCallback?.remove()
            backCallback = object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    when (destination.id) {
                        R.id.favorableRateFragment -> finish()
                        R.id.nbtBankFragment, R.id.internalCoursePage -> {
                            navController.popBackStack(R.id.favorableRateFragment, false)
                            bottomNav.selectedItemId = R.id.nav_favorable
                        }
                    }
                }
            }
            onBackPressedDispatcher.addCallback(this@MainActivity, backCallback!!)
        }
    }
}