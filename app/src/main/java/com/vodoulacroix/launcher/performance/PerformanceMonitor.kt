package com.vodoulacroix.launcher.performance

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.SystemClock
import android.util.Log
import androidx.lifecycle.ProcessLifecycleOwner
import com.vodoulacroix.launcher.BuildConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

/**
 * Performance monitoring system for VodouLauncher
 * 
 * Tracks:
 * 1. Startup time
 * 2. Memory usage
 * 3. Frame drops
 * 4. Background tasks
 * 5. Battery impact
 */
object PerformanceMonitor {
    
    private const val TAG = "PerformanceMonitor"
    private val appStartTime = AtomicLong(0)
    private val performanceData = ConcurrentHashMap<String, Any>()
    private val frameTimes = mutableListOf<Long>()
    private var lastFrameTime = 0L
    private var frameDropCount = 0
    private var isMonitoring = false
    
    // Performance thresholds
    private const val TARGET_STARTUP_TIME_MS = 1500L  // 1.5 seconds
    private const val TARGET_FRAME_TIME_MS = 16L      // 60 FPS
    private const val MAX_FRAME_DROPS = 5             // Allow 5 frame drops
    
    /**
     * Initialize performance monitoring
     */
    fun initialize(application: Application) {
        if (BuildConfig.DEBUG) {
            startMonitoring(application)
        }
    }
    
    /**
     * Start monitoring performance
     */
    private fun startMonitoring(application: Application) {
        isMonitoring = true
        
        // Track app lifecycle
        ProcessLifecycleOwner.get().lifecycle.addObserver(AppLifecycleObserver())
        
        // Start frame monitoring
        startFrameMonitoring()
        
        // Track memory usage periodically
        startMemoryMonitoring(application)
        
        // Track background tasks
        startBackgroundTaskMonitoring()
        
        Log.d(TAG, "Performance monitoring started")
    }
    
    /**
     * Record app startup time
     */
    fun recordAppStart() {
        appStartTime.set(SystemClock.elapsedRealtime())
        performanceData["app_start_time"] = System.currentTimeMillis()
        performanceData["device_model"] = Build.MODEL
        performanceData["android_version"] = Build.VERSION.SDK_INT
    }
    
    /**
     * Record app fully loaded time
     */
    fun recordAppLoaded() {
        val startupTime = SystemClock.elapsedRealtime() - appStartTime.get()
        performanceData["startup_time_ms"] = startupTime
        performanceData["startup_success"] = startupTime <= TARGET_STARTUP_TIME_MS
        
        Log.d(TAG, "App startup time: ${startupTime}ms")
        
        if (startupTime > TARGET_STARTUP_TIME_MS) {
            Log.w(TAG, "Startup time exceeds target of ${TARGET_STARTUP_TIME_MS}ms")
        }
    }
    
    /**
     * Record frame rendering time
     */
    fun recordFrame() {
        if (!isMonitoring) return
        
        val currentTime = SystemClock.elapsedRealtime()
        val frameTime = currentTime - lastFrameTime
        
        if (lastFrameTime > 0) {
            frameTimes.add(frameTime)
            
            // Check for frame drops
            if (frameTime > TARGET_FRAME_TIME_MS * 2) { // More than 2 frames
                frameDropCount++
                Log.w(TAG, "Frame drop detected: ${frameTime}ms")
                
                if (frameDropCount > MAX_FRAME_DROPS) {
                    Log.e(TAG, "Excessive frame drops: $frameDropCount")
                }
            }
            
            // Keep only last 1000 frames
            if (frameTimes.size > 1000) {
                frameTimes.removeAt(0)
            }
        }
        
        lastFrameTime = currentTime
    }
    
    /**
     * Record memory usage
     */
    fun recordMemoryUsage(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            val runtime = Runtime.getRuntime()
            val usedMemory = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024)
            val maxMemory = runtime.maxMemory() / (1024 * 1024)
            
            performanceData["memory_used_mb"] = usedMemory
            performanceData["memory_max_mb"] = maxMemory
            performanceData["memory_percentage"] = (usedMemory.toDouble() / maxMemory * 100).toInt()
            
            if (usedMemory > maxMemory * 0.8) {
                Log.w(TAG, "High memory usage: ${usedMemory}MB (${maxMemory}MB max)")
            }
        }
    }
    
    /**
     * Record background task
     */
    fun recordBackgroundTask(taskName: String, durationMs: Long) {
        performanceData["bg_task_$taskName"] = durationMs
        
        if (durationMs > 1000) { // More than 1 second
            Log.w(TAG, "Long background task: $taskName took ${durationMs}ms")
        }
    }
    
    /**
     * Record ANR risk (long operation on main thread)
     */
    fun recordAnrRisk(operationName: String, durationMs: Long) {
        performanceData["anr_risk_$operationName"] = durationMs
        
        if (durationMs > 100) { // More than 100ms on main thread
            Log.e(TAG, "ANR risk: $operationName took ${durationMs}ms on main thread")
        }
    }
    
    /**
     * Get performance report
     */
    fun getPerformanceReport(): Map<String, Any> {
        val report = mutableMapOf<String, Any>()
        report.putAll(performanceData)
        
        // Calculate average frame time
        if (frameTimes.isNotEmpty()) {
            val avgFrameTime = frameTimes.average()
            report["avg_frame_time_ms"] = avgFrameTime
            report["estimated_fps"] = (1000 / avgFrameTime).toInt()
            report["frame_drop_count"] = frameDropCount
        }
        
        return report
    }
    
    /**
     * Reset performance data
     */
    fun reset() {
        frameTimes.clear()
        frameDropCount = 0
        lastFrameTime = 0L
        performanceData.clear()
    }
    
    /**
     * Stop monitoring
     */
    fun stop() {
        isMonitoring = false
        Log.d(TAG, "Performance monitoring stopped")
    }
    
    /**
     * Start frame monitoring
     */
    private fun startFrameMonitoring() {
        // Frame monitoring would be implemented with Choreographer
        // For now, we rely on manual recordFrame() calls
    }
    
    /**
     * Start memory monitoring
     */
    private fun startMemoryMonitoring(application: Application) {
        CoroutineScope(Dispatchers.IO).launch {
            while (isMonitoring) {
                recordMemoryUsage(application)
                kotlinx.coroutines.delay(30000) // Every 30 seconds
            }
        }
    }
    
    /**
     * Start background task monitoring
     */
    private fun startBackgroundTaskMonitoring() {
        // Background task monitoring implementation
    }
    
    /**
     * Check if performance is within targets
     */
    fun isPerformanceGood(): Boolean {
        val startupTime = performanceData["startup_time_ms"] as? Long ?: 0L
        val frameDrops = frameDropCount
        
        return startupTime <= TARGET_STARTUP_TIME_MS && frameDrops <= MAX_FRAME_DROPS
    }
    
    /**
     * Get performance score (0-100)
     */
    fun getPerformanceScore(): Int {
        var score = 100
        
        // Deduct for slow startup
        val startupTime = performanceData["startup_time_ms"] as? Long ?: 0L
        if (startupTime > TARGET_STARTUP_TIME_MS) {
            val excess = (startupTime - TARGET_STARTUP_TIME_MS).toDouble()
            val penalty = (excess / 1000 * 10).toInt() // 10 points per second over
            score -= minOf(penalty, 30)
        }
        
        // Deduct for frame drops
        if (frameDropCount > 0) {
            val penalty = frameDropCount * 2 // 2 points per frame drop
            score -= minOf(penalty, 20)
        }
        
        // Deduct for high memory usage
        val memoryPercent = performanceData["memory_percentage"] as? Int ?: 0
        if (memoryPercent > 80) {
            val penalty = (memoryPercent - 80) / 2
            score -= minOf(penalty, 20)
        }
        
        return maxOf(score, 0)
    }
}