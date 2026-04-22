package com.vodoulacroix.launcher.performance

import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import java.util.concurrent.atomic.AtomicLong

/**
 * Observes app lifecycle events for performance tracking
 */
class AppLifecycleObserver : LifecycleObserver {
    
    private val appStartTime = AtomicLong(0)
    private val foregroundTime = AtomicLong(0)
    private val backgroundTime = AtomicLong(0)
    
    companion object {
        private const val TAG = "AppLifecycleObserver"
    }
    
    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate() {
        Log.d(TAG, "App created")
        appStartTime.set(System.currentTimeMillis())
        PerformanceMonitor.recordAppStart()
    }
    
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart() {
        Log.d(TAG, "App started")
        foregroundTime.set(System.currentTimeMillis())
        
        // Record transition from background if applicable
        if (backgroundTime.get() > 0) {
            val backgroundDuration = System.currentTimeMillis() - backgroundTime.get()
            PerformanceMonitor.recordBackgroundTask("background_session", backgroundDuration)
            backgroundTime.set(0)
        }
    }
    
    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {
        Log.d(TAG, "App resumed")
        PerformanceMonitor.recordAppLoaded()
    }
    
    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause() {
        Log.d(TAG, "App paused")
    }
    
    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onStop() {
        Log.d(TAG, "App stopped")
        backgroundTime.set(System.currentTimeMillis())
        
        // Record foreground session duration
        if (foregroundTime.get() > 0) {
            val foregroundDuration = System.currentTimeMillis() - foregroundTime.get()
            PerformanceMonitor.recordBackgroundTask("foreground_session", foregroundDuration)
            foregroundTime.set(0)
        }
    }
    
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        Log.d(TAG, "App destroyed")
        
        // Record total app lifetime
        val totalLifetime = System.currentTimeMillis() - appStartTime.get()
        PerformanceMonitor.recordBackgroundTask("app_lifetime", totalLifetime)
        
        // Get final performance report
        val report = PerformanceMonitor.getPerformanceReport()
        logPerformanceReport(report)
    }
    
    /**
     * Log performance report
     */
    private fun logPerformanceReport(report: Map<String, Any>) {
        Log.d(TAG, "=== PERFORMANCE REPORT ===")
        
        report.forEach { (key, value) ->
            when (key) {
                "startup_time_ms" -> {
                    val time = value as Long
                    Log.d(TAG, "Startup time: ${time}ms")
                    if (time > 1500) {
                        Log.w(TAG, "⚠️ Startup time exceeds 1.5s target")
                    }
                }
                "memory_used_mb" -> {
                    val used = value as Long
                    val max = report["memory_max_mb"] as? Long ?: 0L
                    val percent = report["memory_percentage"] as? Int ?: 0
                    Log.d(TAG, "Memory: ${used}MB / ${max}MB (${percent}%)")
                    if (percent > 80) {
                        Log.w(TAG, "⚠️ High memory usage (>80%)")
                    }
                }
                "avg_frame_time_ms" -> {
                    val avgTime = value as Double
                    val fps = (1000 / avgTime).toInt()
                    Log.d(TAG, "Frame rate: ${fps} FPS (avg ${String.format("%.2f", avgTime)}ms)")
                    if (fps < 50) {
                        Log.w(TAG, "⚠️ Low frame rate (<50 FPS)")
                    }
                }
                "frame_drop_count" -> {
                    val drops = value as Int
                    Log.d(TAG, "Frame drops: $drops")
                    if (drops > 5) {
                        Log.w(TAG, "⚠️ Excessive frame drops (>5)")
                    }
                }
            }
        }
        
        val score = PerformanceMonitor.getPerformanceScore()
        Log.d(TAG, "Performance score: ${score}/100")
        
        if (score < 70) {
            Log.w(TAG, "⚠️ Performance needs improvement (score < 70)")
        } else if (score < 85) {
            Log.i(TAG, "✅ Performance is acceptable (score: $score)")
        } else {
            Log.i(TAG, "🎉 Performance is excellent (score: $score)")
        }
        
        Log.d(TAG, "=== END REPORT ===")
    }
}