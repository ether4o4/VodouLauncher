package com.fourgauteau.os

import android.view.Gravity
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class VistaTaskbarAppAdapter(
    private var apps: List<VistaLauncherActivity.VistaApp>,
    private val onClick: (VistaLauncherActivity.VistaApp) -> Unit
) : RecyclerView.Adapter<VistaTaskbarAppAdapter.ViewHolder>() {
    class ViewHolder(val textView: TextView) : RecyclerView.ViewHolder(textView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent.context.vistaTextView())
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val app = apps[position]
        holder.textView.text = app.name
        holder.textView.setOnClickListener { onClick(app) }
    }

    override fun getItemCount(): Int = apps.size

    fun updateApps(nextApps: List<VistaLauncherActivity.VistaApp>) {
        apps = nextApps
        notifyDataSetChanged()
    }
}

class VistaStartMenuAdapter(
    private var apps: List<VistaLauncherActivity.VistaApp>,
    private val onClick: (VistaLauncherActivity.VistaApp) -> Unit
) : RecyclerView.Adapter<VistaStartMenuAdapter.ViewHolder>() {
    class ViewHolder(val textView: TextView) : RecyclerView.ViewHolder(textView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent.context.vistaTextView())
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val app = apps[position]
        holder.textView.text = app.name
        holder.textView.setOnClickListener { onClick(app) }
    }

    override fun getItemCount(): Int = apps.size

    fun updateApps(nextApps: List<VistaLauncherActivity.VistaApp>) {
        apps = nextApps
        notifyDataSetChanged()
    }
}

class VistaNotificationAdapter(
    private val notifications: List<String>
) : RecyclerView.Adapter<VistaNotificationAdapter.ViewHolder>() {
    class ViewHolder(val textView: TextView) : RecyclerView.ViewHolder(textView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent.context.vistaTextView())
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textView.text = notifications[position]
    }

    override fun getItemCount(): Int = notifications.size
}

private fun android.content.Context.vistaTextView(): TextView {
    return TextView(this).apply {
        gravity = Gravity.CENTER_VERTICAL
        minHeight = 48
        setPadding(16, 8, 16, 8)
        setTextColor(android.graphics.Color.WHITE)
    }
}
