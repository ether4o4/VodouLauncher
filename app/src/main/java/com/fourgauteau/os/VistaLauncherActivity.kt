package com.fourgauteau.os

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.webkit.JavascriptInterface
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.fourgauteau.os.databinding.VistaDesktopBinding

class VistaLauncherActivity : AppCompatActivity() {
    private lateinit var binding: VistaDesktopBinding
    private var isStartMenuVisible = false
    private var isNotificationCenterVisible = false
    private var isSearchBarVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_4GAuteauOS_VistaEvolved_Blue)
        super.onCreate(savedInstanceState)

        binding = VistaDesktopBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.vistaDesktopGrid.layoutManager = GridLayoutManager(this, 4)
        binding.vistaDesktopGrid.adapter = VistaDesktopIconAdapter(defaultApps()) { }

        binding.vistaTaskbarApps.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.vistaTaskbarApps.adapter = VistaTaskbarAppAdapter(defaultApps()) { }

        binding.vistaStartMenuApps.layoutManager = LinearLayoutManager(this)
        binding.vistaStartMenuApps.adapter = VistaStartMenuAdapter(defaultApps()) { }

        binding.vistaNotificationsList.layoutManager = LinearLayoutManager(this)
        binding.vistaNotificationsList.adapter = VistaNotificationAdapter(emptyList())

        binding.vistaClock.text = java.text.SimpleDateFormat("h:mm a", java.util.Locale.getDefault()).format(java.util.Date())
        binding.vistaStartButton.setOnClickListener { toggleStartMenu() }
        binding.vistaBattery.setOnClickListener { toggleNotificationCenter() }
        binding.vistaWifi.setOnClickListener { toggleNotificationCenter() }
        binding.vistaVolume.setOnClickListener { toggleNotificationCenter() }
        binding.root.setOnLongClickListener {
            toggleSearchBar()
            true
        }

        showSplash()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun showSplash() {
        val splash: WebView = binding.neversoftSplash
        splash.settings.javaScriptEnabled = true
        splash.setBackgroundColor(Color.BLACK)
        splash.isVerticalScrollBarEnabled = false
        splash.isHorizontalScrollBarEnabled = false
        splash.addJavascriptInterface(SplashBridge(), "AndroidSplash")
        splash.loadUrl("file:///android_asset/neversoft_splash.html")
        // Robust fallback: fade the splash out even if the page never signals done.
        splash.postDelayed({ dismissSplash() }, 2900L)
    }

    private fun dismissSplash() {
        val splash: WebView = binding.neversoftSplash
        if (splash.visibility != View.VISIBLE) return
        splash.animate()
            .alpha(0f)
            .setDuration(400)
            .withEndAction {
                splash.visibility = View.GONE
                splash.loadUrl("about:blank")
            }
            .start()
    }

    /** Bridge so the splash page can call window.AndroidSplash.done() (or __neverSoftSplashDone). */
    private inner class SplashBridge {
        @JavascriptInterface
        fun done() {
            runOnUiThread { dismissSplash() }
        }
    }

    private fun defaultApps(): List<VistaApp> = listOf(
        VistaApp("Files", android.R.drawable.ic_menu_upload),
        VistaApp("Settings", android.R.drawable.ic_menu_manage),
        VistaApp("Search", android.R.drawable.ic_menu_search),
        VistaApp("Info", android.R.drawable.ic_dialog_info)
    )

    private fun toggleStartMenu() {
        isStartMenuVisible = !isStartMenuVisible
        binding.vistaStartMenu.visibility = if (isStartMenuVisible) View.VISIBLE else View.GONE
    }

    private fun toggleNotificationCenter() {
        isNotificationCenterVisible = !isNotificationCenterVisible
        binding.vistaNotificationCenter.visibility = if (isNotificationCenterVisible) View.VISIBLE else View.GONE
    }

    private fun toggleSearchBar() {
        isSearchBarVisible = !isSearchBarVisible
        binding.vistaSearchBar.visibility = if (isSearchBarVisible) View.VISIBLE else View.GONE
    }

    data class VistaApp(
        val name: String,
        val iconRes: Int,
        val packageName: String = "",
        val isRunning: Boolean = false,
        val notificationCount: Int = 0
    )

    companion object {
        fun createIntent(context: Context): Intent {
            return Intent(context, VistaLauncherActivity::class.java)
        }
    }
}
