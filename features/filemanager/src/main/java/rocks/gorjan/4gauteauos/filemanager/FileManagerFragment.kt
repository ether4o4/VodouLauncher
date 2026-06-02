package rocks.gorjan.fourgauteauos.filemanager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import rocks.gorjan.fourgauteauos.filemanager.adapter.FileListAdapter
import rocks.gorjan.fourgauteauos.filemanager.viewmodel.FileManagerViewModel

/**
 * FileManagerFragment — Double-Pane Master-Detail File Explorer
 *
 * Implements the "Windows PC Vibe" design directive with a resizable
 * two-pane layout:
 *   - Left pane (master): scrollable file/directory tree
 *   - Right pane (detail): file metadata, preview, and actions
 *   - Draggable divider between panes for resizing
 *
 * Layout weight ratio defaults to 0.45 (master) : 0.55 (detail),
 * matching SnapManager.DEFAULT_SPLIT_RATIO for visual consistency.
 */
class FileManagerFragment : Fragment() {

    companion object {
        const val TAG = "FileManagerFragment"
        const val DEFAULT_MASTER_WEIGHT = 0.45f
    }

    private lateinit var viewModel: FileManagerViewModel
    private lateinit var fileListAdapter: FileListAdapter

    // UI components
    private lateinit var masterRecyclerView: RecyclerView
    private lateinit var detailContainer: ViewGroup
    private lateinit var dividerView: View
    private lateinit var detailFileName: TextView
    private lateinit var detailFilePath: TextView
    private lateinit var detailFileSize: TextView
    private lateinit var detailLastModified: TextView
    private lateinit var emptyDetailHint: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_file_manager, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[FileManagerViewModel::class.java]

        bindViews(view)
        setupMasterList()
        setupDividerDrag()
        observeViewModel()

        // Load initial directory (external storage root by default)
        viewModel.loadDirectory(null)
    }

    private fun bindViews(view: View) {
        masterRecyclerView = view.findViewById(R.id.file_list)
        detailContainer = view.findViewById(R.id.detail_pane)
        dividerView = view.findViewById(R.id.pane_divider)
        detailFileName = view.findViewById(R.id.detail_file_name)
        detailFilePath = view.findViewById(R.id.detail_file_path)
        detailFileSize = view.findViewById(R.id.detail_file_size)
        detailLastModified = view.findViewById(R.id.detail_last_modified)
        emptyDetailHint = view.findViewById(R.id.empty_detail_hint)
    }

    private fun setupMasterList() {
        fileListAdapter = FileListAdapter { fileItem ->
            viewModel.selectFile(fileItem)
        }

        masterRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = fileListAdapter
        }
    }

    /**
     * Implements a draggable divider between the master and detail panes.
     * Uses onTouchListener with horizontal translation to resize the panes
     * in real time, providing a desktop-like split-pane experience.
     */
    private fun setupDividerDrag() {
        var startX = 0f
        var startWeight = DEFAULT_MASTER_WEIGHT

        dividerView.setOnTouchListener { _, event ->
            when (event.action) {
                android.view.MotionEvent.ACTION_DOWN -> {
                    startX = event.rawX
                    val masterLp = masterRecyclerView.layoutParams as? androidx.constraintlayout.widget.ConstraintLayout.LayoutParams
                    startWeight = masterLp?.matchConstraintPercentWidth ?: DEFAULT_MASTER_WEIGHT
                    true
                }
                android.view.MotionEvent.ACTION_MOVE -> {
                    val deltaX = event.rawX - startX
                    val parentWidth = (dividerView.parent as? View)?.width ?: return@setOnTouchListener true
                    if (parentWidth <= 0) return@setOnTouchListener true

                    val deltaWeight = deltaX / parentWidth.toFloat()
                    val newWeight = (startWeight + deltaWeight).coerceIn(0.25f, 0.65f)

                    val masterLp = masterRecyclerView.layoutParams as? androidx.constraintlayout.widget.ConstraintLayout.LayoutParams
                    masterLp?.matchConstraintPercentWidth = newWeight
                    masterRecyclerView.layoutParams = masterLp

                    true
                }
                android.view.MotionEvent.ACTION_UP, android.view.MotionEvent.ACTION_CANCEL -> {
                    // Save the preferred split ratio
                    val masterLp = masterRecyclerView.layoutParams as? androidx.constraintlayout.widget.ConstraintLayout.LayoutParams
                    masterLp?.matchConstraintPercentWidth?.let { ratio ->
                        viewModel.saveSplitRatio(ratio)
                    }
                    true
                }
                else -> false
            }
        }
    }

    private fun observeViewModel() {
        viewModel.files.observe(viewLifecycleOwner) { files ->
            fileListAdapter.submitList(files)
        }

        viewModel.selectedFile.observe(viewLifecycleOwner) { file ->
            if (file != null) {
                detailFileName.text = file.name
                detailFilePath.text = file.path
                detailFileSize.text = file.formattedSize
                detailLastModified.text = file.lastModifiedFormatted
                emptyDetailHint.visibility = View.GONE
            } else {
                emptyDetailHint.visibility = View.VISIBLE
            }
        }
    }
}
