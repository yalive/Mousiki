package com.cas.musicplayer.ui.artists.list


import android.os.Bundle
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cas.musicplayer.R
import com.cas.musicplayer.base.common.Resource
import com.cas.musicplayer.base.views.SideBar
import com.cas.musicplayer.ui.BaseFragment
import com.cas.musicplayer.ui.artists.artistdetail.ArtistFragment
import com.cas.musicplayer.utils.Extensions.injector
import com.cas.musicplayer.utils.gone
import com.cas.musicplayer.utils.observe
import com.cas.musicplayer.utils.visible
import com.cas.musicplayer.viewmodel.activityViewModel
import com.google.android.material.appbar.CollapsingToolbarLayout
import kotlinx.android.synthetic.main.fragment_artists.*


class ArtistListFragment : BaseFragment<ArtistListViewModel>() {

    override val layoutResourceId: Int = R.layout.fragment_artists
    override val viewModel by activityViewModel { injector.artistListViewModel }

    private lateinit var adapter: ArtistListAdapter
    private lateinit var sideBar: SideBar
    private lateinit var txtDialog: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sideBar = view.findViewById(R.id.sideBar)
        txtDialog = view.findViewById(R.id.txtDialog)

        val collapsingToolbar =
            activity?.findViewById<CollapsingToolbarLayout>(R.id.collapsingToolbar)
        collapsingToolbar?.isTitleEnabled = false
        collapsingToolbar?.title = "Artists"
        val rltContainer = activity?.findViewById<RelativeLayout>(R.id.rltContainer)

        rltContainer?.gone()
        setupRecyclerView()
        observeViewModel()
        viewModel.loadAllArtists()
        setupSideBar()
    }

    private fun setupRecyclerView() {
        adapter = ArtistListAdapter(onClickArtist = {
            val bundle = Bundle()
            bundle.putParcelable(ArtistFragment.EXTRAS_ARTIST, it)
            findNavController().navigate(R.id.artistFragment, bundle)
        })
        recycler.adapter = adapter
        val layoutManager = recycler.layoutManager as LinearLayoutManager
        var firstVisibleInListview = layoutManager.findFirstVisibleItemPosition()
        recycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val currentFirstVisible = layoutManager.findFirstVisibleItemPosition()
                firstVisibleInListview = currentFirstVisible
                val item = adapter.dataItems[firstVisibleInListview]
                val char = item.name[0]
                sideBar.setChooseLetter(char)
            }
        })
    }

    private fun observeViewModel() {
        observe(viewModel.artists) { resource ->
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
                    val layoutManager = recycler.layoutManager as LinearLayoutManager
                    layoutManager.scrollToPositionWithOffset(headPositionInAdapter, 0)
                    layoutManager.stackFromEnd = true
                }
            }
        })
        sideBar.setChoosePos(1)
    }
}
