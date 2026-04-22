package com.vodoulacroix.launcher.helper

import android.content.Context
import android.widget.Toast
import com.vodoulacroix.launcher.data.Prefs

object CustomWelcome {
    
    private const val WELCOME_SHOWN_KEY = "welcome_shown_v2"
    private const val WELCOME_MESSAGE = "Welcome to VodouLauncher! 🎵"
    
    fun showWelcomeIfNeeded(context: Context, prefs: Prefs) {
        val welcomeShown = prefs.getBoolean(WELCOME_SHOWN_KEY, false)
        
        if (!welcomeShown) {
            // Show welcome toast
            Toast.makeText(context, WELCOME_MESSAGE, Toast.LENGTH_LONG).show()
            
            // Mark as shown
            prefs.setBoolean(WELCOME_SHOWN_KEY, true)
            
            // You could also log this event or show a dialog here
            logEvent("first_launch")
        }
    }
    
    private fun logEvent(event: String) {
        // Placeholder for analytics or logging
        // In a real app, you might want to log this to Firebase or similar
        println("VodouLauncher event: $event")
    }
    
    fun getCustomGreeting(): String {
        val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
        return when {
            hour < 12 -> "Good morning! 🎶"
            hour < 18 -> "Good afternoon! 🎵"
            else -> "Good evening! 🎼"
        }
    }
    
    fun getVersionInfo(): String {
        return "VodouLauncher v1.0\nCustomized for VodouLaCroix"
    }
}