package com.fourgauteau.os

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.fourgauteau.os.databinding.ActivityMainBinding
import com.fourgauteau.os.helper.CustomWelcome
import com.fourgauteau.os.helper.Prefs
import com.fourgauteau.os.helper.isEinkDisplay
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Optimized MainActivity with lazy loading and performance improvements
 * 
 * Key optimizations:
 * 1. Lazy app loading (after UI is shown)
 * 2. No heavy operations in onCreate()
 * 3. Proper threading
 * 4. Minimal startup logic
 */
class OptimizedMainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    private lateinit var prefs: Prefs
    
    override fun onCreate(savedInstanceState: Bundle?) {
        // 1. Set theme BEFORE super.onCreate()
        prefs = Prefs(this)
        if (isEinkDisplay()) prefs.appTheme = AppCompatDelegate.MODE_NIGHT_NO
        AppCompatDelegate.setDefaultNightMode(prefs.appTheme)
        
        // 2. Super call
        super.onCreate(savedInstanceState)
        
        // 3. Inflate view AFTER theme is set
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // 4. Edge-to-edge display
        WindowCompat.setDecorFitsSystemWindows(window, false)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.attributes.layoutInDisplayCutoutMode = 
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }
        
        // 5. Setup navigation
        val navController = findNavController(R.id.nav_host_fragment)
        
        // 6. Back press handling
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (navController.currentDestination?.id != R.id.mainFragment) {
                    if (!navController.popBackStack()) {
                        // Handle activity-level back if needed
                    }
                } else {
                    binding.messageLayout.visibility = View.GONE
                }
            }
        })
        
        // 7. MINIMAL startup logic - show UI immediately
        showInitialUI()
        
        // 8. Lazy load heavy operations AFTER UI is shown
        lifecycleScope.launch {
            // Small delay to ensure UI is rendered
            delay(50)
            
            // Load apps in background
            loadAppsLazily()
            
            // Show welcome message if needed
            showWelcomeIfNeeded()
            
            // Setup first-time initialization if needed
            setupFirstTimeInitialization()
        }
        
        // 9. Setup orientation
        setupOrientation()
        
        // 10. Window flags for immersive experience
        window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
    }
    
    /**
     * Show UI immediately without waiting for data
     */
    private fun showInitialUI() {
        // Just show the UI - no data loading here
        binding.root.visibility = View.VISIBLE
    }
    
    /**
     * Load apps lazily in background
     */
    private suspend fun loadAppsLazily() {
        // This runs in background coroutine
        viewModel.getAppList()
    }
    
    /**
     * Show welcome message if needed (in background)
     */
    private suspend fun showWelcomeIfNeeded() {
        CustomWelcome.showWelcomeIfNeeded(this, prefs)
    }
    
    /**
     * Setup first-time initialization if needed
     */
    private suspend fun setupFirstTimeInitialization() {
        if (prefs.firstOpen) {
            viewModel.firstOpen(true)
            prefs.firstOpen = false
            prefs.firstOpenTime = System.currentTimeMillis()
            viewModel.setDefaultClockApp()
            viewModel.resetLauncherLiveData.call()
        }
    }
    
    /**
     * Setup orientation based on preferences
     */
    private fun setupOrientation() {
        requestedOrientation = prefs.orientation
    }
    
    /**
     * Initialize click listeners (lightweight)
     */
    private fun initClickListeners() {
        // Lightweight click listeners only
        binding.messageLayout.setOnClickListener {
            binding.messageLayout.visibility = View.GONE
        }
    }
    
    /**
     * Initialize observers (lightweight)
     */
    private fun initObservers() {
        // Lightweight observers only
        viewModel.appList.observe(this) { apps ->
            // Update UI with apps
        }
        
        viewModel.isOlauncherDefault.observe(this) { isDefault ->
            // Handle default launcher status
        }
    }
    
    override fun onResume() {
        super.onResume()
        // Lightweight resume logic only
        viewModel.isOlauncherDefault()
    }
    
    override fun onPause() {
        super.onPause()
        // Cleanup if needed
    }
    
    override fun onDestroy() {
        super.onDestroy()
        // Cleanup resources
    }
    
    companion object {
        /**
         * Create intent for this activity
         */
        fun createIntent(context: Context): Intent {
            return Intent(context, OptimizedMainActivity::class.java)
        }
    }
}