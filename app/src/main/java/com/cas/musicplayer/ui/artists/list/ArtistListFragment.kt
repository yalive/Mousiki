package com.cas.musicplayer.ui.artists.list


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.cas.musicplayer.R
import com.cas.musicplayer.base.common.Status
import com.cas.musicplayer.base.views.SideBar
import com.cas.musicplayer.ui.BaseFragment
import com.cas.musicplayer.ui.artists.artistdetail.ArtistFragment
import com.cas.musicplayer.utils.Extensions.injector
import com.cas.musicplayer.utils.gone
import com.cas.musicplayer.utils.visible
import com.cas.musicplayer.viewmodel.activityViewModel
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.fragment_artists.*


class ArtistListFragment : BaseFragment() {

    val TAG = "ArtistsFragmentLog"

    lateinit var adapter: ArtistListAdapter

    lateinit var sideBar: SideBar
    lateinit var txtDialog: TextView

    private val viewModel by activityViewModel { injector.artistListViewModel }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(com.cas.musicplayer.R.layout.fragment_artists, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sideBar = view.findViewById(R.id.sideBar)
        txtDialog = view.findViewById(R.id.txtDialog)

        val collapsingToolbar =
            activity?.findViewById<CollapsingToolbarLayout>(com.cas.musicplayer.R.id.collapsingToolbar)

        collapsingToolbar?.isTitleEnabled = false

        collapsingToolbar?.title = "Artists"

        val rltContainer = activity?.findViewById<RelativeLayout>(com.cas.musicplayer.R.id.rltContainer)

        val imgCollapsed = activity?.findViewById<CircleImageView>(com.cas.musicplayer.R.id.imgCollapsed)
        rltContainer?.gone()

        setupRecyclerView()

        observeViewModel()

        viewModel.loadAllArtists()

        setupSideBar()

    }

    private fun setupRecyclerView() {
        adapter = ArtistListAdapter(emptyList(), onClickArtist = {
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

                if (currentFirstVisible > firstVisibleInListview)
                    Log.d(TAG, "scroll up!")
                else
                    Log.d(TAG, "scroll down!")

                firstVisibleInListview = currentFirstVisible

                val item = adapter.items[firstVisibleInListview]
                val char = item.name[0]
                Log.i(TAG, "firstVisibleInListview = $firstVisibleInListview -> $char")

                sideBar.setChooseLetter(char)
            }
        })
    }

    private fun observeViewModel() {
        viewModel.artistResources.observe(this, Observer { resource ->
            if (resource.status == Status.SUCCESS) {
                adapter.items = resource.data!!
                listContainer.visible()
                progressBar.gone()
            } else if (resource.status == Status.LOADING) {
                progressBar.visible()
            }
        })
    }


    private fun setupSideBar() {
        sideBar.setTextView(txtDialog)
        sideBar.setTouchable(true)

        sideBar.setOnTouchingLetterChangedListener(object : SideBar.OnTouchingLetterChangedListener {
            override fun onTouchingLetterChanged(str: String) {
                val headPositionInAdapter = adapter.getLetterPosition(str)
                if (headPositionInAdapter != -1) {
                    Log.d(TAG, "On Touch side bar: $headPositionInAdapter")
                    val layoutManager = recycler.layoutManager as LinearLayoutManager
                    layoutManager.scrollToPositionWithOffset(headPositionInAdapter, 0)
                    layoutManager.stackFromEnd = true
                }
            }
        })
        sideBar.setChoosePos(1)
    }

}