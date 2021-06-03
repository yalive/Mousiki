package com.cas.musicplayer.ui.library

import android.graphics.Rect
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.cas.common.extensions.onClick
import com.cas.common.viewmodel.viewModel
import com.cas.musicplayer.R
import com.cas.musicplayer.databinding.FragmentLibraryBinding
import com.cas.musicplayer.di.Injector
import com.cas.musicplayer.tmp.observe
import com.cas.musicplayer.ui.MainActivity
import com.cas.musicplayer.ui.base.BaseFragment
import com.cas.musicplayer.ui.common.songs.AppImage
import com.cas.musicplayer.ui.common.songs.BaseSongsFragment
import com.cas.musicplayer.ui.library.adapters.LibraryAdapter
import com.cas.musicplayer.ui.playlist.custom.CustomPlaylistSongsFragment
import com.cas.musicplayer.utils.DeviceInset
import com.cas.musicplayer.utils.dpToPixel
import com.cas.musicplayer.utils.navigateSafeAction
import com.cas.musicplayer.utils.viewBinding
import com.facebook.ads.AdSize
import com.facebook.ads.AdView
import com.mousiki.shared.ui.library.LibraryViewModel

/**
 ***************************************
 * Created by Abdelhadi on 2019-11-28.
 ***************************************
 */
class LibraryFragment : BaseFragment<LibraryViewModel>(
    R.layout.fragment_library
) {

    override val viewModel by viewModel { Injector.libraryViewModel }
    override val screenTitle by lazy { "" }
    private val binding by viewBinding(FragmentLibraryBinding::bind)

    private val adapter by lazy {
        LibraryAdapter(viewModel)
    }

    private var adView: AdView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adjustStatusBarWithTheme()
        setupBannerAd()
        binding.recyclerView.adapter = adapter
        binding.recyclerView.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(
                outRect: Rect, view: View,
                parent: RecyclerView, state: RecyclerView.State
            ) {
                val context = parent.context
                val position = parent.getChildAdapterPosition(view)
                with(outRect) {
                    if (position > 1) {
                        top = context.dpToPixel(24f)
                    }
                }
            }
        })
        binding.btnSettings.onClick {
            findNavController().navigateSafeAction(R.id.action_libraryFragment_to_settingsFragment)
        }

        observe(DeviceInset) { inset ->
            binding.mainView.updatePadding(top = inset.top)
        }

        observeViewModel()
    }

    override fun withToolbar(): Boolean = false

    override fun onDestroyView() {
        adView?.destroy()
        super.onDestroyView()
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadCustomPlaylists()
    }

    private fun observeViewModel() {
        observe(viewModel.recentSongs.asLiveData()) {
            if (it == null) return@observe
            adapter.updateRecent(it)
        }
        observe(viewModel.heavySongs.asLiveData()) {
            if (it == null) return@observe
            adapter.updateHeavy(it)
        }
        observe(viewModel.favouriteSongs.asLiveData()) {
            if (it == null) return@observe
            adapter.updateFavourite(it)
        }
        observe(viewModel.playlists.asLiveData()) {
            if (it == null) return@observe
            adapter.updatePlaylists(it)
        }
        observe(viewModel.onClickSong.asLiveData()) {
            it?.getContentIfNotHandled()?.let {
                (activity as? MainActivity)?.collapseBottomPanel()
            }
        }
        observe(viewModel.onClickPlaylist.asLiveData()) {
            it?.getContentIfNotHandled()?.let { playList ->
                findNavController().navigate(
                    R.id.action_libraryFragment_to_customPlaylistSongsFragment,
                    bundleOf(
                        BaseSongsFragment.EXTRAS_ID_FEATURED_IMAGE to AppImage.AppImageUrl(
                            playList.urlImage
                        ),
                        CustomPlaylistSongsFragment.EXTRAS_PLAYLIST to playList
                    )
                )
            }
        }
    }

    private fun setupBannerAd() {
        if (!viewModel.bannerAdOn()) {
            binding.bannerContainer.isVisible = false
            return
        }
        adView = AdView(
            requireContext(),
            "IMG_16_9_APP_INSTALL#YOUR_PLACEMENT_ID",
            AdSize.BANNER_HEIGHT_50
        )
        binding.bannerContainer.addView(adView)
        adView!!.loadAd()
    }
}