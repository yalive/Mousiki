package com.cas.musicplayer.ui.common.multiselection

import android.os.Bundle
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import com.cas.common.extensions.onClick
import com.cas.common.viewmodel.viewModel
import com.cas.musicplayer.R
import com.cas.musicplayer.databinding.MultiSelectTracksFragmentBinding
import com.cas.musicplayer.player.PlayerQueue
import com.cas.musicplayer.tmp.observe
import com.cas.musicplayer.ui.base.BaseFragment
import com.cas.musicplayer.ui.playlist.select.AddTrackToPlaylistFragment
import com.cas.musicplayer.utils.*
import com.mousiki.shared.domain.models.Track
import kotlinx.coroutines.delay

class MultiSelectTrackFragment : BaseFragment<MultiSelectTracksViewModel>(
    R.layout.multi_select_tracks_fragment
) {
    override val screenName: String = "MultiSelectTrackFragment"
    override val viewModel by viewModel {
        val tracks = requireArguments().getParcelableArray(EXTRAS_TRACKS)!!.toList()
        val track = requireArguments().getParcelable<Track?>(EXTRAS_TRACK)
        @Suppress("UNCHECKED_CAST") // Checked
        MultiSelectTracksViewModel(track, tracks as List<Track>)
    }

    private val binding by viewBinding(MultiSelectTracksFragmentBinding::bind)
    private val adapter by lazy { MultiSelectTracksAdapter(viewModel) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.setOnClickListener {
            // Just to prevent underline click detection
        }
        binding.recyclerView.adapter = adapter
        binding.btnClose.onClick { activity?.onBackPressed() }
        binding.btnSelectAll.onClick { viewModel.selectAll() }
        binding.btnCancel.onClick { viewModel.deselectAll() }
        binding.btnPlayNext.onClick {
            val tracks = viewModel.selectedTracks()
            PlayerQueue.addAsNext(*tracks.toTypedArray())
            context?.toast(R.string.added_to_play_next)
            activity?.onBackPressed()
        }
        binding.btnAddToPlaylist.onClick {
            AddTrackToPlaylistFragment.present(
                fm = requireActivity().supportFragmentManager,
                onAddedToPlaylist = { activity?.onBackPressed() },
                tracks = viewModel.selectedTracks()
            )
        }
        observeChanges()
    }

    private fun observeChanges() {
        observe(DeviceInset) { inset ->
            binding.topBar.updateLayoutParams<ConstraintLayout.LayoutParams> {
                topMargin = inset.top
            }
            binding.recyclerView.updatePadding(bottom = inset.bottom)
        }

        observe(viewModel.selectionCount.asLiveData()) { count ->
            binding.txtTitle.text = if (count == 0) "" else "$count"

            binding.btnPlayNext.isEnabled = count > 0
            binding.btnAddToPlaylist.isEnabled = count > 0
        }

        observe(viewModel.cancelVisible.asLiveData()) { cancelVisible ->
            binding.btnCancel.isVisible = cancelVisible
            binding.btnSelectAll.isVisible = !cancelVisible
        }

        viewLifecycleOwner.lifecycleScope.launchWhenResumed {
            val animDuration = resources.getInteger(android.R.integer.config_mediumAnimTime)
            delay(animDuration.toLong())
            observe(viewModel.tracks.asLiveData()) {
                val items = it ?: return@observe
                adapter.submitList(items)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        onBackPressCallback {
            isEnabled = false // Disable back press listener
            slideDown()
        }
    }

    companion object {

        private const val EXTRAS_TRACKS = "tracks"
        private const val EXTRAS_TRACK = "track"

        fun present(
            activity: FragmentActivity,
            tracks: List<Track>,
            track: Track? = null
        ) {
            activity.slideUpFragment<MultiSelectTrackFragment>().apply {
                arguments = bundleOf(
                    EXTRAS_TRACK to track,
                    EXTRAS_TRACKS to tracks.toTypedArray()
                )
            }
        }
    }
}