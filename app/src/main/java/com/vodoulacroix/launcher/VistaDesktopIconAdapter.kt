package com.vodoulacroix.launcher

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import androidx.recyclerview.widget.RecyclerView
import com.vodoulacroix.launcher.VistaLauncherActivity.VistaApp
import com.vodoulacroix.launcher.databinding.VistaDesktopIconBinding

/**
 * Adapter for Vista desktop icons with glass effects and animations
 */
class VistaDesktopIconAdapter(
    private var apps: List<VistaApp>,
    private val onAppClick: (VistaApp) -> Unit
) : RecyclerView.Adapter<VistaDesktopIconAdapter.VistaIconViewHolder>() {
    
    private var selectedPosition = -1
    
    class VistaIconViewHolder(
        private val binding: VistaDesktopIconBinding,
        private val onAppClick: (VistaApp) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(app: VistaApp, isSelected: Boolean) {
            binding.iconImage.setImageResource(app.iconRes)
            binding.iconLabel.text = app.name
            
            // Show/hide running indicator
            binding.runningIndicator.visibility = if (app.isRunning) View.VISIBLE else View.GONE
            
            // Show/hide notification badge
            if (app.notificationCount > 0) {
                binding.notificationBadge.visibility = View.VISIBLE
                binding.notificationBadge.text = app.notificationCount.toString()
            } else {
                binding.notificationBadge.visibility = View.GONE
            }
            
            // Selection state
            binding.iconSelection.visibility = if (isSelected) View.VISIBLE else View.GONE
            
            // Click listener
            binding.root.setOnClickListener {
                onAppClick(app)
                animateIconClick(binding.iconContainer)
            }
            
            // Long click for context menu
            binding.root.setOnLongClickListener {
                showContextMenu(binding.contextMenuButton)
                true
            }
            
            // Context menu button
            binding.contextMenuButton.setOnClickListener {
                showContextMenu(it)
            }
        }
        
        /**
         * Animate icon click with Vista-style bounce
         */
        private fun animateIconClick(view: View) {
            val animator = ValueAnimator.ofFloat(1f, 0.8f, 1.1f, 1f).apply {
                duration = 400
                interpolator = OvershootInterpolator(1.5f)
                addUpdateListener { animation ->
                    val value = animation.animatedValue as Float
                    view.scaleX = value
                    view.scaleY = value
                }
            }
            animator.start()
        }
        
        /**
         * Show context menu
         */
        private fun showContextMenu(view: View) {
            // Animate context menu button
            view.visibility = View.VISIBLE
            view.scaleX = 0f
            view.scaleY = 0f
            view.alpha = 0f
            
            val animator = ValueAnimator.ofFloat(0f, 1f).apply {
                duration = 200
                interpolator = OvershootInterpolator()
                addUpdateListener { animation ->
                    val value = animation.animatedValue as Float
                    view.scaleX = value
                    view.scaleY = value
                    view.alpha = value
                }
                addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        // Show context menu dialog
                        // For now, just hide after delay
                        view.postDelayed({
                            hideContextMenu(view)
                        }, 2000)
                    }
                })
            }
            animator.start()
        }
        
        /**
         * Hide context menu
         */
        private fun hideContextMenu(view: View) {
            val animator = ValueAnimator.ofFloat(1f, 0f).apply {
                duration = 150
                addUpdateListener { animation ->
                    val value = animation.animatedValue as Float
                    view.scaleX = value
                    view.scaleY = value
                    view.alpha = value
                }
                addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        view.visibility = View.GONE
                    }
                })
            }
            animator.start()
        }
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VistaIconViewHolder {
        val binding = VistaDesktopIconBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return VistaIconViewHolder(binding, onAppClick)
    }
    
    override fun onBindViewHolder(holder: VistaIconViewHolder, position: Int) {
        val app = apps[position]
        val isSelected = position == selectedPosition
        holder.bind(app, isSelected)
        
        // Add hover effects for devices with pointer
        holder.itemView.setOnHoverListener { view, event ->
            when (event.action) {
                android.view.MotionEvent.ACTION_HOVER_ENTER -> {
                    animateHoverEnter(view)
                    true
                }
                android.view.MotionEvent.ACTION_HOVER_EXIT -> {
                    animateHoverExit(view)
                    true
                }
                else -> false
            }
        }
    }
    
    override fun getItemCount(): Int = apps.size
    
    /**
     * Update apps list
     */
    fun updateApps(newApps: List<VistaApp>) {
        apps = newApps
        notifyDataSetChanged()
    }
    
    /**
     * Select an icon
     */
    fun selectIcon(position: Int) {
        val previousSelected = selectedPosition
        selectedPosition = position
        if (previousSelected >= 0) {
            notifyItemChanged(previousSelected)
        }
        if (position >= 0) {
            notifyItemChanged(position)
        }
    }
    
    /**
     * Clear selection
     */
    fun clearSelection() {
        val previousSelected = selectedPosition
        selectedPosition = -1
        if (previousSelected >= 0) {
            notifyItemChanged(previousSelected)
        }
    }
    
    /**
     * Animate hover enter
     */
    private fun animateHoverEnter(view: View) {
        val animator = ValueAnimator.ofFloat(1f, 1.05f).apply {
            duration = 150
            addUpdateListener { animation ->
                val value = animation.animatedValue as Float
                view.scaleX = value
                view.scaleY = value
            }
        }
        animator.start()
    }
    
    /**
     * Animate hover exit
     */
    private fun animateHoverExit(view: View) {
        val animator = ValueAnimator.ofFloat(1.05f, 1f).apply {
            duration = 150
            addUpdateListener { animation ->
                val value = animation.animatedValue as Float
                view.scaleX = value
                view.scaleY = value
            }
        }
        animator.start()
    }
}