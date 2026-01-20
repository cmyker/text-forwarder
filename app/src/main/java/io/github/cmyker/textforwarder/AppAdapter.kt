package io.github.cmyker.textforwarder

import android.content.Context
import android.content.pm.ResolveInfo
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AppAdapter(
    private val context: Context,
    private val apps: List<ResolveInfo>,
    private val onAppSelected: (ResolveInfo) -> Unit
) : RecyclerView.Adapter<AppAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val appIcon: ImageView = view.findViewById(R.id.app_icon)
        val appLabel: TextView = view.findViewById(R.id.app_label)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.list_item_app, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val app = apps[position]
        val packageManager = context.packageManager
        holder.appIcon.setImageDrawable(app.loadIcon(packageManager))
        holder.appLabel.text = app.loadLabel(packageManager).toString()
        holder.itemView.setOnClickListener { onAppSelected(app) }
    }

    override fun getItemCount() = apps.size
}
