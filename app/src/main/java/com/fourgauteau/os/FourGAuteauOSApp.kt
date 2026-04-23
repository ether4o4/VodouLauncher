package com.fourgauteau.os

import android.app.Application
import android.content.Context
import androidx.lifecycle.ProcessLifecycleOwner
import com.fourgauteau.os.performance.PerformanceMonitor
import com.fourgauteau.os.performance.AppLifecycleObserver
import timber.log.Timber

/**
 * Performance-optimized Application class for 4GAuteauOS
 * 
 * Features:
 * 1. Performance monitoring
 * 2. Memory optimization
 * 3. Crash reporting setup
 * 4. Dependency initialization
 */
class FourGAuteauOSApp : Application() {

    companion object {
        private lateinit var instance: FourGAuteauOSApp

        fun getAppContext(): Context = instance.applicationContext
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        // Initialize performance monitoring
        initializePerformanceMonitoring()

        // Initialize logging
        initializeLogging()

        // Initialize app lifecycle observer
        initializeLifecycleObserver()

        // Initialize dependencies
        initializeDependencies()

        // Log app startup
        Timber.d("4GAuteauOS application created")
    }

    /**
     * Initialize performance monitoring
     */
    private fun initializePerformanceMonitoring() {
        PerformanceMonitor.initialize(this)
        Timber.d("Performance monitoring initialized")
    }

    /**
     * Initialize logging system
     */
    private fun initializeLogging() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
            Timber.d("Debug logging enabled")
        } else {
            // In production, use a crash reporting tree
            // Timber.plant(CrashReportingTree())
        }
    }

    /**
     * Initialize app lifecycle observer
     */
    private fun initializeLifecycleObserver() {
        ProcessLifecycleOwner.get().lifecycle.addObserver(AppLifecycleObserver())
        Timber.d("App lifecycle observer initialized")
    }

    /**
     * Initialize app dependencies
     */
    private fun initializeDependencies() {
        // Initialize any app-wide dependencies here
        // Keep this lightweight - don't initialize heavy libraries

        Timber.d("App dependencies initialized")
    }

    override fun onLowMemory() {
        super.onLowMemory()
        Timber.w("Low memory warning - clearing caches")

        // Clear any caches or temporary data
        clearMemoryCaches()
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        Timber.d("Trim memory level: $level")

        when (level) {
            TRIM_MEMORY_RUNNING_MODERATE,
            TRIM_MEMORY_RUNNING_LOW,
            TRIM_MEMORY_RUNNING_CRITICAL -> {
                // App is running, reduce memory usage
                clearNonEssentialMemory()
            }
            TRIM_MEMORY_BACKGROUND,
            TRIM_MEMORY_MODERATE,
            TRIM_MEMORY_COMPLETE -> {
                // App is in background, clear more memory
                clearBackgroundMemory()
            }
            TRIM_MEMORY_UI_HIDDEN -> {
                // UI is hidden, clear UI-related memory
                clearUIMemory()
            }
        }
    }

    /**
     * Clear memory caches
     */
    private fun clearMemoryCaches() {
        // Clear image caches, bitmap pools, etc.
        // This should be implemented by specific cache managers
    }

    /**
     * Clear non-essential memory
     */
    private fun clearNonEssentialMemory() {
        // Clear temporary data, non-essential caches
    }

    /**
     * Clear background memory
     */
    private fun clearBackgroundMemory() {
        // Clear more aggressive caches when in background
    }

    /**
     * Clear UI memory
     */
    private fun clearUIMemory() {
        // Clear UI-related memory when UI is hidden
    }

    /**
     * Get performance report
     */
    fun getPerformanceReport(): Map<String, Any> {
        return PerformanceMonitor.getPerformanceReport()
    }

    /**
     * Check if performance is good
     */
    fun isPerformanceGood(): Boolean {
        return PerformanceMonitor.isPerformanceGood()
    }

    /**
     * Get performance score
     */
    fun getPerformanceScore(): Int {
        return PerformanceMonitor.getPerformanceScore()
    }
}