package com.vodoulacroix.launcher

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowManager
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.OvershootInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.cardview.widget.CardView
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vodoulacroix.launcher.databinding.VistaDesktopBinding
import com.vodoulacroix.launcher.helper.CustomWelcome
import com.vodoulacroix.launcher.helper.Prefs
import com.vodoulacroix.launcher.helper.isEinkDisplay
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Vista Launcher Activity - Provides the familiar Windows Vista feeling
 * but evolved with futuristic, modern, ultra HD smooth looks.
 * 
 * Features:
 * - Vista-style glass desktop
 * - Taskbar with Start button
 * - Desktop icons with glass effects
 * - Start menu with apps
 * - Notification center
 * - Search bar
 * - Smooth Vista-style animations
 */
class VistaLauncherActivity : AppCompatActivity() {
    
    private lateinit var binding: VistaDesktopBinding
    private lateinit var prefs: Prefs
    
    // Animation states
    private var isStartMenuVisible = false
    private var isNotificationCenterVisible = false
    private var isSearchBarVisible = false
    
    // Performance tracking
    private var activityStartTime = 0L
    
    override fun onCreate(savedInstanceState: Bundle?) {
        // Start performance tracking
        activityStartTime = System.currentTimeMillis()
        
        // Set Vista theme
        prefs = Prefs(this)
        if (isEinkDisplay()) prefs.appTheme = AppCompatDelegate.MODE_NIGHT_NO
        AppCompatDelegate.setDefaultNightMode(prefs.appTheme)
        
        // Apply Vista theme
        setTheme(R.style.Theme_VodouLauncher_VistaEvolved_Blue)
        
        super.onCreate(savedInstanceState)
        
        // Inflate Vista desktop layout
        binding = VistaDesktopBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Edge-to-edge display for modern look
        WindowCompat.setDecorFitsSystemWindows(window, false)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.attributes.layoutInDisplayCutoutMode = 
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }
        
        // Window flags for glass effect
        window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        
        // Setup Vista UI components
        setupVistaDesktop()
        setupVistaTaskbar()
        setupVistaStartMenu()
        setupVistaNotificationCenter()
        setupVistaSearchBar()
        
        // Load apps lazily
        loadAppsLazily()
        
        // Show welcome if needed
        showVistaWelcome()
        
        // Setup clock
        setupVistaClock()
        
        // Performance tracking
        trackPerformance()
    }
    
    /**
     * Setup Vista desktop with glass effects
     */
    private fun setupVistaDesktop() {
        // Setup desktop icons grid
        val desktopLayoutManager = GridLayoutManager(this, 4)
        binding.vistaDesktopGrid.layoutManager = desktopLayoutManager
        binding.vistaDesktopGrid.adapter = VistaDesktopIconAdapter(emptyList()) { app ->
            launchApp(app)
        }
        
        // Add glass effect listeners
        binding.vistaDesktopGrid.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                // Could add parallax or other effects here
            }
        })
    }
    
    /**
     * Setup Vista taskbar with Start button
     */
    private fun setupVistaTaskbar() {
        // Start button click
        binding.vistaStartButton.setOnClickListener {
            toggleStartMenu()
        }
        
        // Taskbar apps
        val taskbarLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.vistaTaskbarApps.layoutManager = taskbarLayoutManager
        binding.vistaTaskbarApps.adapter = VistaTaskbarAppAdapter(emptyList()) { app ->
            launchApp(app)
        }
        
        // System tray clicks
        binding.vistaClock.setOnClickListener {
            toggleNotificationCenter()
        }
        
        binding.vistaBattery.setOnClickListener {
            showBatteryInfo()
        }
        
        binding.vistaWifi.setOnClickListener {
            showNetworkInfo()
        }
        
        binding.vistaVolume.setOnClickListener {
            showVolumeControl()
        }
    }
    
    /**
     * Setup Vista start menu
     */
    private fun setupVistaStartMenu() {
        // Start menu apps
        val startMenuLayoutManager = GridLayoutManager(this, 2)
        binding.vistaStartMenuApps.layoutManager = startMenuLayoutManager
        binding.vistaStartMenuApps.adapter = VistaStartMenuAdapter(emptyList()) { app ->
            launchApp(app)
            hideStartMenu()
        }
        
        // Power button
        binding.vistaStartMenu.findViewById<View>(R.id.vista_power_button).setOnClickListener {
            showPowerMenu()
        }
        
        // Settings button
        binding.vistaStartMenu.findViewById<View>(R.id.vista_settings_button).setOnClickListener {
            launchSettings()
            hideStartMenu()
        }
    }
    
    /**
     * Setup Vista notification center
     */
    private fun setupVistaNotificationCenter() {
        // Notifications list
        val notificationsLayoutManager = LinearLayoutManager(this)
        binding.vistaNotificationsList.layoutManager = notificationsLayoutManager
        binding.vistaNotificationsList.adapter = VistaNotificationAdapter(emptyList())
        
        // Quick settings
        setupQuickSettings()
    }
    
    /**
     * Setup Vista search bar
     */
    private fun setupVistaSearchBar() {
        binding.vistaSearchInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH) {
                performSearch(binding.vistaSearchInput.text.toString())
                return@setOnEditorActionListener true
            }
            false
        }
        
        // Global search trigger (could be gesture or hardware button)
        // For now, we'll use a long press on desktop
        binding.vistaDesktopGrid.setOnLongClickListener {
            toggleSearchBar()
            true
        }
    }
    
    /**
     * Toggle Start menu with Vista-style animation
     */
    private fun toggleStartMenu() {
        if (isStartMenuVisible) {
            hideStartMenu()
        } else {
            showStartMenu()
        }
    }
    
    private fun showStartMenu() {
        if (isStartMenuVisible) return
        
        isStartMenuVisible = true
        
        // Hide other overlays
        hideNotificationCenter()
        hideSearchBar()
        
        // Animate start menu
        binding.vistaStartMenu.visibility = View.VISIBLE
        binding.vistaStartMenu.scaleX = 0.8f
        binding.vistaStartMenu.scaleY = 0.8f
        binding.vistaStartMenu.alpha = 0f
        
        val animator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 300
            interpolator = OvershootInterpolator(0.8f)
            addUpdateListener { animation ->
                val value = animation.animatedValue as Float
                binding.vistaStartMenu.scaleX = 0.8f + 0.2f * value
                binding.vistaStartMenu.scaleY = 0.8f + 0.2f * value
                binding.vistaStartMenu.alpha = value
            }
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    binding.vistaStartMenu.scaleX = 1f
                    binding.vistaStartMenu.scaleY = 1f
                    binding.vistaStartMenu.alpha = 1f
                }
            })
        }
        animator.start()
    }
    
    private fun hideStartMenu() {
        if (!isStartMenuVisible) return
        
        isStartMenuVisible = false
        
        val animator = ValueAnimator.ofFloat(1f, 0f).apply {
            duration = 200
            interpolator = AccelerateDecelerateInterpolator()
            addUpdateListener { animation ->
                val value = animation.animatedValue as Float
                binding.vistaStartMenu.scaleX = value
                binding.vistaStartMenu.scaleY = value
                binding.vistaStartMenu.alpha = value
            }
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    binding.vistaStartMenu.visibility = View.GONE
                }
            })
        }
        animator.start()
    }
    
    /**
     * Toggle Notification center
     */
    private fun toggleNotificationCenter() {
        if (isNotificationCenterVisible) {
            hideNotificationCenter()
        } else {
            showNotificationCenter()
        }
    }
    
    private fun showNotificationCenter() {
        if (isNotificationCenterVisible) return
        
        isNotificationCenterVisible = true
        
        // Hide other overlays
        hideStartMenu()
        hideSearchBar()
        
        binding.vistaNotificationCenter.visibility = View.VISIBLE
        binding.vistaNotificationCenter.translationX = 100f
        binding.vistaNotificationCenter.alpha = 0f
        
        val animator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 250
            interpolator = AccelerateDecelerateInterpolator()
            addUpdateListener { animation ->
                val value = animation.animatedValue as Float
                binding.vistaNotificationCenter.translationX = 100f * (1 - value)
                binding.vistaNotificationCenter.alpha = value
            }
        }
        animator.start()
    }
    
    private fun hideNotificationCenter() {
        if (!isNotificationCenterVisible) return
        
        isNotificationCenterVisible = false
        
        val animator = ValueAnimator.ofFloat(1f, 0f).apply {
            duration = 200
            interpolator = AccelerateDecelerateInterpolator()
            addUpdateListener { animation ->
                val value = animation.animatedValue as Float
                binding.vistaNotificationCenter.translationX = 100f * (1 - value)
                binding.vistaNotificationCenter.alpha = value
            }
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    binding.vistaNotificationCenter.visibility = View.GONE
                    binding.vistaNotificationCenter.translationX = 0f
                }
            })
        }
        animator.start()
    }
    
    /**
     * Toggle Search bar
     */
    private fun toggleSearchBar() {
        if (isSearchBarVisible) {
            hideSearchBar()
        } else {
            showSearchBar()
        }
    }
    
    private fun showSearchBar() {
        if (isSearchBarVisible) return
        
        isSearchBarVisible = true
        
        // Hide other overlays
        hideStartMenu()
        hideNotificationCenter()
        
        binding.vistaSearchBar.visibility = View.VISIBLE
        binding.vistaSearchBar.translationY = -50f
        binding.vistaSearchBar.alpha = 0f
        
        val animator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 250
            interpolator = OvershootInterpolator(0.5f)
            addUpdateListener { animation ->
                val value = animation.animatedValue as Float
                binding.vistaSearchBar.translationY = -50f * (1 - value)
                binding.vistaSearchBar.alpha = value
            }
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    binding.vistaSearchInput.requestFocus()
                    // Show keyboard
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
                    imm.showSoftInput(binding.vistaSearchInput, 0)
                }
            })
        }
        animator.start()
    }
    
    private fun hideSearchBar() {
        if (!isSearchBarVisible) return
        
        isSearchBarVisible = false
        
        // Hide keyboard
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
        imm.hideSoftInputFromWindow(binding.vistaSearchInput.windowToken, 0)
        
        val animator = ValueAnimator.ofFloat(1f, 0f).apply {
            duration = 200
            interpolator = AccelerateDecelerateInterpolator()
            addUpdateListener { animation ->
                val value = animation.animatedValue as Float
                binding.vistaSearchBar.translationY = -50f * (1 - value)
                binding.vistaSearchBar.alpha = value
            }
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    binding.vistaSearchBar.visibility = View.GONE
                    binding.vistaSearchBar.translationY = 0f
                    binding.vistaSearchInput.text.clear()
                }
            })
        }
        animator.start()
    }
    
    /**
     * Load apps lazily in background
     */
    private fun loadAppsLazily() {
        lifecycleScope.launch {
            // Simulate loading delay
            delay(100)
            
            // Load desktop apps
            val desktopApps = loadDesktopApps()
            (binding.vistaDesktopGrid.adapter as? VistaDesktopIconAdapter)?.updateApps(desktopApps)
            
            // Load taskbar apps
            val taskbarApps = loadTaskbarApps()
            (binding.vistaTaskbarApps.adapter as? VistaTaskbarAppAdapter)?.updateApps(taskbarApps)
            
            // Load start menu apps
            val startMenuApps = loadStartMenuApps()
            (binding.vistaStartMenuApps.adapter as? VistaStartMenuAdapter)?.updateApps(startMenuApps)
        }
    }
    
    /**
     * Show Vista-style welcome
     */
    private fun showVistaWelcome() {
        lifecycleScope.launch {
            delay(500) // Wait for UI to settle
            CustomWelcome.showWelcomeIfNeeded(this@VistaLauncherActivity, prefs)
        }
    }
    
    /**
     * Setup Vista clock
     */
    private fun setupVistaClock() {
        val handler = Handler(Looper.getMainLooper())
        val updateClock = object : Runnable {
            override fun run() {
                val time = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault()).format(java.util.Date())
                binding.vistaClock.text = time
                handler.postDelayed(this, 60000) // Update every minute
            }
        }
        updateClock.run()
    }
    
    /**
     * Track performance
     */
    private fun trackPerformance() {
        lifecycleScope.launch {
            delay(1000) // Wait 1 second for UI to load
            val loadTime = System.currentTimeMillis() - activityStartTime
            println("VistaLauncherActivity loaded in ${loadTime}ms")
            
            if (loadTime > 1500) {
                println("⚠️ Vista launcher startup time exceeds 1.5s target")
            }
        }
    }
    
    // Placeholder methods for app loading and actions
    private fun loadDesktopApps(): List<VistaApp> = emptyList()
    private fun loadTaskbarApps(): List<VistaApp> = emptyList()
    private fun loadStartMenuApps(): List<VistaApp> = emptyList()
    private fun launchApp(app: VistaApp) {}
    private fun showBatteryInfo() {}
    private fun showNetworkInfo() {}
    private fun showVolumeControl() {}
    private fun showPowerMenu() {}
    private fun launchSettings() {}
    private fun performSearch(query: String) {}
    private fun setupQuickSettings() {}
    
    override fun onBackPressed() {
        // Handle back button based on what's visible
        when {
            isStartMenuVisible -> hideStartMenu()
            isNotificationCenterVisible -> hideNotificationCenter()
            isSearchBarVisible -> hideSearchBar()
            else -> super.onBackPressed()
        }
    }
    
    override fun onResume() {
        super.onResume()
        // Refresh UI if needed
    }
    
    override fun onPause() {
        super.onPause()
        // Clean up if needed
    }
    
    data class VistaApp(
        val name: String,
        val packageName: String,
        val iconRes: Int,
        val isRunning: Boolean = false,
        val notificationCount: Int = 0
    )
    
    companion object {
        fun createIntent(context: Context): Intent {
            return Intent(context, VistaLauncherActivity::class.java)
        }
    }
}