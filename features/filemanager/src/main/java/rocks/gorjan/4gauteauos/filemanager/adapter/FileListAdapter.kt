package rocks.gorjan.fourgauteauos.filemanager.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import rocks.gorjan.fourgauteauos.filemanager.model.FileItem

/**
 * FileListAdapter — RecyclerView adapter for the file manager's master pane.
 * Uses ListAdapter with DiffUtil for efficient list updates.
 */
class FileListAdapter(
    private val onItemClick: (FileItem) -> Unit
) : ListAdapter<FileItem, FileListAdapter.ViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_2, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, onItemClick)
    }

    class ViewHolder(view: android.view.View) : RecyclerView.ViewHolder(view) {
        private val text1: android.widget.TextView = view.findViewById(android.R.id.text1)
        private val text2: android.widget.TextView = view.findViewById(android.R.id.text2)

        fun bind(item: FileItem, onClick: (FileItem) -> Unit) {
            val icon = if (item.isDirectory) "📁" else "📄"
            text1.text = "$icon  ${item.name}"
            text2.text = if (item.isDirectory) "Directory" else "${item.formattedSize}  •  ${item.lastModifiedFormatted}"
            itemView.setOnClickListener { onClick(item) }
        }
    }

    object DiffCallback : DiffUtil.ItemCallback<FileItem>() {
        override fun areItemsTheSame(old: FileItem, new: FileItem): Boolean =
            old.path == new.path

        override fun areContentsTheSame(old: FileItem, new: FileItem): Boolean =
            old == new
    }
}
