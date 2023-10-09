package com.amora.movieku

import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.amora.movieku.databinding.ActivityMainBinding
import com.amora.movieku.ui.home.popular.PopularFragment
import com.amora.movieku.ui.home.upcoming.UpcomingFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), NavController.OnDestinationChangedListener {

	private lateinit var binding: ActivityMainBinding
	private lateinit var navController: NavController
	private var backPressCallback: OnBackPressedCallback? = null
	private var backPressCount = 0
	private val mainCoroutine = CoroutineScope(Dispatchers.Main)
	private var backPressJob: Job? = null
	private var popularFragment: PopularFragment? = null
	private var upcomingFragment: UpcomingFragment? = null

	private fun getAppBarToolbar() = binding.toolbar

	private fun enableBackPress() {
		removeBackPress()
		backPressCallback = object : OnBackPressedCallback(true) {
			override fun handleOnBackPressed() {
				navController.navigateUp()
			}
		}
	}

	private fun removeBackPress() {
		backPressCallback?.remove()
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		enableBackPress()
		binding = ActivityMainBinding.inflate(layoutInflater)
		setContentView(binding.root)
		setSupportActionBar(binding.toolbar)
		val navView: BottomNavigationView = binding.navView
		WindowCompat.setDecorFitsSystemWindows(window, false)

		val navHostFragment =
			supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
		navController = navHostFragment.navController
		navController.addOnDestinationChangedListener(this)
		// Passing each menu ID as a set of Ids because each
		// menu should be considered as top level destinations.
		val appBarConfiguration = AppBarConfiguration(
			setOf(
				R.id.navigation_upcoming, R.id.navigation_popular, R.id.navigation_search
			)
		)
		setupActionBarWithNavController(navController, appBarConfiguration)
		navView.setupWithNavController(navController)
	}

	private fun supportToolBar(isVisible: Boolean? = false) {
		if (isVisible != null) {
			binding.clToolbar.isVisible = isVisible
		}
	}

	private fun setToolbarVisibility(isVisible: Boolean = true) {
		getAppBarToolbar().isVisible = false
		binding.navView.isVisible = isVisible
	}

	fun setPopularFragment(fragment: PopularFragment) {
		popularFragment = fragment
	}

	fun setUpcomingFragment(fragment: UpcomingFragment) {
		upcomingFragment = fragment
	}

	override fun onDestinationChanged(
		controller: NavController,
		destination: NavDestination,
		arguments: Bundle?
	) {
		when (destination.id) {
			R.id.navigation_detail -> {
				supportToolBar()
				setToolbarVisibility(false)
				enableBackPress()
			}

			else -> {
				setToolbarVisibility()
				supportToolBar(true)
				enableBackPressExit()
			}
		}
	}

	override fun onSupportNavigateUp(): Boolean {
		return navController.navigateUp() || super.onSupportNavigateUp()
	}

	private fun enableBackPressExit() {
		removeBackPress()
		backPressCallback = object : OnBackPressedCallback(true) {
			override fun handleOnBackPressed() {
				if (backPressCount == 0) {
					Toast.makeText(
						this@MainActivity,
						"Tekan sekali untuk keluar",
						Toast.LENGTH_SHORT
					).show()
					backPressCount++
					backPressJob = mainCoroutine.launch {
						delay(2000)
						backPressCount = 0
					}
				} else {
					this@MainActivity.finish()
				}
			}
		}

		addBackpressCallback()
	}

	private fun addBackpressCallback() {
		if (backPressCallback != null) {
			this@MainActivity.onBackPressedDispatcher.addCallback(backPressCallback!!)
		}
	}
}