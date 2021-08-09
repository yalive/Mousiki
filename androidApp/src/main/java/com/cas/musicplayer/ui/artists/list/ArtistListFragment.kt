package com.cas.musicplayer.ui.artists.list


import android.os.Bundle
import android.view.View
import androidx.core.view.updatePadding
import androidx.core.widget.doAfterTextChanged
import androidx.navigation.fragment.findNavController
import com.cas.common.extensions.gone
import com.cas.common.extensions.hideSoftKeyboard
import com.cas.common.extensions.visible
import com.cas.common.viewmodel.activityViewModel
import com.cas.musicplayer.R
import com.cas.musicplayer.databinding.FragmentArtistsBinding
import com.cas.musicplayer.di.Injector
import com.cas.musicplayer.tmp.observe
import com.cas.musicplayer.ui.artists.EXTRAS_ARTIST
import com.cas.musicplayer.ui.base.BaseFragment
import com.cas.musicplayer.ui.base.adjustStatusBarWithTheme
import com.cas.musicplayer.ui.base.setupToolbar
import com.cas.musicplayer.ui.common.songs.AppImage
import com.cas.musicplayer.ui.common.songs.BaseSongsFragment
import com.cas.musicplayer.utils.DeviceInset
import com.cas.musicplayer.utils.navigateSafeAction
import com.cas.musicplayer.utils.viewBinding
import com.mousiki.shared.ui.resource.Resource


class ArtistListFragment : BaseFragment<ArtistListViewModel>(
    R.layout.fragment_artists
) {

    override val screenName: String = "ArtistListFragment"
    override val viewModel by activityViewModel { Injector.artistListViewModel }
    private val binding by viewBinding(FragmentArtistsBinding::bind)

    private lateinit var adapter: ArtistListAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adjustStatusBarWithTheme()
        setupToolbar(binding.toolbarView.toolbar, R.string.artists)
        setupRecyclerView()
        observeViewModel()
        viewModel.loadAllArtists()
        binding.editSearch.doAfterTextChanged {
            filterArtists()
        }
        filterArtists()
        observe(DeviceInset) { inset ->
            binding.root.updatePadding(top = inset.top)
        }
    }

    override fun onPause() {
        super.onPause()
        view?.hideSoftKeyboard()
    }

    private fun filterArtists() {
        viewModel.filterArtists(binding.editSearch.text?.toString().orEmpty())
    }

    private fun setupRecyclerView() {
        adapter = ArtistListAdapter(onClickArtist = {
            view?.hideSoftKeyboard()
            val bundle = Bundle()
            bundle.putParcelable(EXTRAS_ARTIST, it)
            bundle.putParcelable(
                BaseSongsFragment.EXTRAS_ID_FEATURED_IMAGE,
                AppImage.AppImageUrl(it.imageFullPath)
            )
            findNavController().navigateSafeAction(
                R.id.action_artistsFragment_to_artistSongsFragment,
                bundle
            )
        })
        binding.recyclerView.adapter = adapter
    }

    private fun observeViewModel() {
        observe(viewModel.filteredArtists) { resource ->
            when (resource) {
                is Resource.Success -> {
                    adapter.dataItems = resource.data.toMutableList()
                    binding.recyclerView.visible()
                    binding.progressBar.gone()
                }
                is Resource.Loading -> binding.progressBar.visible()
            }
        }
    }
}
