package rocks.gorjan.fourgauteauos.filemanager.viewmodel

import android.os.Environment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import rocks.gorjan.fourgauteauos.filemanager.model.FileItem
import java.io.File

/**
 * FileManagerViewModel — Manages file system browsing state for the double-pane explorer.
 */
class FileManagerViewModel : ViewModel() {

    private val _files = MutableLiveData<List<FileItem>>(emptyList())
    val files: LiveData<List<FileItem>> = _files

    private val _selectedFile = MutableLiveData<FileItem?>(null)
    val selectedFile: LiveData<FileItem?> = _selectedFile

    private val _currentDirectory = MutableLiveData<String>()
    val currentDirectory: LiveData<String> = _currentDirectory

    private var splitRatio: Float = 0.45f

    fun loadDirectory(path: String?) {
        viewModelScope.launch {
            val dir = if (path != null) {
                File(path)
            } else {
                Environment.getExternalStorageDirectory()
            }

            val items = withContext(Dispatchers.IO) {
                dir.listFiles()
                    ?.map { FileItem.fromFile(it) }
                    ?.sortedWith(compareByDescending<FileItem> { it.isDirectory }.thenBy { it.name.lowercase() })
                    ?: emptyList()
            }

            _files.value = items
            _currentDirectory.value = dir.absolutePath
        }
    }

    fun selectFile(fileItem: FileItem) {
        _selectedFile.value = fileItem
        if (fileItem.isDirectory) {
            loadDirectory(fileItem.path)
        }
    }

    fun saveSplitRatio(ratio: Float) {
        splitRatio = ratio.coerceIn(0.25f, 0.65f)
    }

    fun getSplitRatio(): Float = splitRatio
}
