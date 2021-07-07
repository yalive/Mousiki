package com.cas.musicplayer.ui.artists.list


import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.view.updatePadding
import androidx.core.widget.doAfterTextChanged
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cas.common.extensions.gone
import com.cas.common.extensions.hideSoftKeyboard
import com.cas.common.extensions.visible
import com.cas.common.viewmodel.activityViewModel
import com.cas.musicplayer.R
import com.cas.musicplayer.databinding.FragmentArtistsBinding
import com.cas.musicplayer.di.Injector
import com.cas.musicplayer.tmp.observe
import com.cas.musicplayer.ui.artists.EXTRAS_ARTIST
import com.cas.musicplayer.ui.artists.sidebar.SideBar
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
    private lateinit var sideBar: SideBar
    private lateinit var txtDialog: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adjustStatusBarWithTheme()
        setupToolbar(binding.toolbarView.toolbar, R.string.artists)
        sideBar = view.findViewById(R.id.sideBar)
        txtDialog = view.findViewById(R.id.txtDialog)
        setupRecyclerView()
        observeViewModel()
        viewModel.loadAllArtists()
        setupSideBar()
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
        val layoutManager = binding.recyclerView.layoutManager as LinearLayoutManager
        var firstVisibleInListview = layoutManager.findFirstVisibleItemPosition()
        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val currentFirstVisible = layoutManager.findFirstVisibleItemPosition()
                firstVisibleInListview = currentFirstVisible
                if (firstVisibleInListview < adapter.dataItems.size && firstVisibleInListview >= 0) {
                    val item = adapter.dataItems[firstVisibleInListview]
                    val char = item.name[0]
                    sideBar.setChooseLetter(char)
                }
            }
        })
    }

    private fun observeViewModel() {
        observe(viewModel.filteredArtists) { resource ->
            when (resource) {
                is Resource.Success -> {
                    adapter.dataItems = resource.data.toMutableList()
                    binding.listContainer.visible()
                    binding.progressBar.gone()
                }
                is Resource.Loading -> binding.progressBar.visible()
            }
        }
    }

    private fun setupSideBar() {
        sideBar.setTextView(txtDialog)
        sideBar.setTouchable(true)
        sideBar.setOnTouchingLetterChangedListener(object :
            SideBar.OnTouchingLetterChangedListener {
            override fun onTouchingLetterChanged(str: String) {
                val headPositionInAdapter = adapter.getLetterPosition(str)
                if (headPositionInAdapter != -1) {
                    val layoutManager = binding.recyclerView.layoutManager as LinearLayoutManager
                    layoutManager.scrollToPositionWithOffset(headPositionInAdapter, 0)
                    layoutManager.stackFromEnd = true
                }
            }
        })
        sideBar.setChoosePos(1)
    }
}
