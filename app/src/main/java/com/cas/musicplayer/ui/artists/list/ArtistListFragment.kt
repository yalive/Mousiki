package com.cas.musicplayer.ui.artists.list


import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.widget.doAfterTextChanged
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cas.common.extensions.gone
import com.cas.common.extensions.hideSoftKeyboard
import com.cas.common.extensions.observe
import com.cas.common.extensions.visible
import com.cas.common.fragment.BaseFragment
import com.cas.common.resource.Resource
import com.cas.common.viewmodel.activityViewModel
import com.cas.musicplayer.R
import com.cas.musicplayer.di.injector.injector
import com.cas.musicplayer.ui.artists.EXTRAS_ARTIST
import com.cas.musicplayer.ui.artists.sidebar.SideBar
import com.cas.musicplayer.ui.common.songs.BaseSongsFragment
import com.cas.musicplayer.ui.common.songs.FeaturedImage
import kotlinx.android.synthetic.main.fragment_artists.*


class ArtistListFragment : BaseFragment<ArtistListViewModel>() {

    override val layoutResourceId: Int = R.layout.fragment_artists
    override val viewModel by activityViewModel { injector.artistListViewModel }
    override val screenTitle by lazy {
        getString(R.string.artists)
    }
    private lateinit var adapter: ArtistListAdapter
    private lateinit var sideBar: SideBar
    private lateinit var txtDialog: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        darkStatusBarOnDarkMode()
        sideBar = view.findViewById(R.id.sideBar)
        txtDialog = view.findViewById(R.id.txtDialog)
        setupRecyclerView()
        observeViewModel()
        viewModel.loadAllArtists()
        setupSideBar()
        editSearch.doAfterTextChanged {
            filterArtists()
        }
        filterArtists()
    }

    override fun onPause() {
        super.onPause()
        view?.hideSoftKeyboard()
    }

    private fun filterArtists() {
        viewModel.filterArtists(editSearch.text?.toString() ?: "")
    }

    private fun setupRecyclerView() {
        adapter = ArtistListAdapter(onClickArtist = {
            view?.hideSoftKeyboard()
            val bundle = Bundle()
            bundle.putParcelable(EXTRAS_ARTIST, it)
            bundle.putParcelable(
                BaseSongsFragment.EXTRAS_ID_FEATURED_IMAGE,
                FeaturedImage.FeaturedImageUrl(it.imageFullPath)
            )
            findNavController().navigate(R.id.action_artistsFragment_to_artistSongsFragment, bundle)
        })
        recyclerView.adapter = adapter
        val layoutManager = recyclerView.layoutManager as LinearLayoutManager
        var firstVisibleInListview = layoutManager.findFirstVisibleItemPosition()
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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
                    listContainer.visible()
                    progressBar.gone()
                }
                is Resource.Loading -> progressBar.visible()
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
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    layoutManager.scrollToPositionWithOffset(headPositionInAdapter, 0)
                    layoutManager.stackFromEnd = true
                }
            }
        })
        sideBar.setChoosePos(1)
    }
}
