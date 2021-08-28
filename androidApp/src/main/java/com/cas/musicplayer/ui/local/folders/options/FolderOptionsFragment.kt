package com.cas.musicplayer.ui.local.folders.options

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.WhichButton
import com.afollestad.materialdialogs.actions.getActionButton
import com.cas.common.extensions.onClick
import com.cas.musicplayer.R
import com.cas.musicplayer.databinding.FragmentFolderOptionsBinding
import com.cas.musicplayer.ui.local.folders.Folder
import com.cas.musicplayer.ui.local.folders.FolderType
import com.cas.musicplayer.utils.PreferenceUtil
import com.cas.musicplayer.utils.ensureRoundedBackground
import com.cas.musicplayer.utils.viewBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class FolderOptionsFragment : BottomSheetDialogFragment() {

    private val binding by viewBinding(FragmentFolderOptionsBinding::bind)
    private var onOption: ((FolderOption) -> Unit)? = null
    private val folder by lazy { requireArguments().getParcelable<Folder>(EXTRA_FOLDER)!! }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_folder_options, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ensureRoundedBackground()
        with(binding) {
            val count = folder.ids.count()
            val songsCount = requireContext().resources.getQuantityString(
                R.plurals.numberOfSongs, count, count
            )
            txtTrackTitle.text = folder.name
            txtTrackArtist.text = songsCount

            btnHide.onClick {
                MaterialDialog(requireContext()).show {
                    val message =
                        getString(R.string.dialog_message_confirm_hide_folder, folder.name)
                    message(text = message)
                    title(R.string.dialog_title_confirm_hide_folder)
                    positiveButton(R.string.label_hide) {
                        if (folder.folderType == FolderType.SONG) {
                            PreferenceUtil.toggleFolderVisibility(folder.path)
                        } else if (folder.folderType == FolderType.VIDEO) {
                            PreferenceUtil.toggleVideosFolderVisibility(folder.path)
                        }

                        onOption?.invoke(FolderOption.Hidden)
                        this@FolderOptionsFragment.dismiss()
                    }
                    negativeButton(R.string.cancel)
                    getActionButton(WhichButton.NEGATIVE).updateTextColor(Color.parseColor("#808184"))
                }
            }
        }
    }

    companion object {

        private const val EXTRA_FOLDER = "folder"

        fun present(
            fm: FragmentManager,
            folder: Folder,
            onOption: (FolderOption) -> Unit = {}
        ) {
            val fragment = FolderOptionsFragment()
            fragment.arguments = bundleOf(
                EXTRA_FOLDER to folder
            )
            fragment.onOption = onOption
            fragment.show(fm, fragment.tag)
        }
    }
}