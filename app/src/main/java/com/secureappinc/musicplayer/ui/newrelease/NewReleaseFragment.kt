package com.secureappinc.musicplayer.ui.newrelease


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.lifecycle.Observer
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.gson.Gson
import com.secureappinc.musicplayer.R
import com.secureappinc.musicplayer.base.common.Resource
import com.secureappinc.musicplayer.base.common.Status
import com.secureappinc.musicplayer.data.enteties.MusicTrack
import com.secureappinc.musicplayer.ui.BaseFragment
import com.secureappinc.musicplayer.ui.MainActivity
import com.secureappinc.musicplayer.ui.bottomsheet.FvaBottomSheetFragment
import com.secureappinc.musicplayer.utils.Extensions.injector
import com.secureappinc.musicplayer.utils.gone
import com.secureappinc.musicplayer.utils.visible
import com.secureappinc.musicplayer.viewmodel.viewModel
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.fragment_new_release.*
import javax.inject.Inject

class NewReleaseFragment : BaseFragment(), NewReleaseVideoAdapter.onItemClickListener {

    val TAG = "NewReleaseFragment"

    lateinit var adapter: NewReleaseVideoAdapter

    private val viewModel by viewModel { injector.newReleaseViewModel }

    @Inject
    lateinit var gson: Gson

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new_release, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        injector.inject(this)

        activity?.title = "New Release"

        val collapsingToolbar = activity?.findViewById<CollapsingToolbarLayout>(R.id.collapsingToolbar)

        collapsingToolbar?.isTitleEnabled = true

        collapsingToolbar?.title = "New Release"

        val rltContainer = activity?.findViewById<RelativeLayout>(R.id.rltContainer)

        val imgCollapsed = activity?.findViewById<CircleImageView>(R.id.imgCollapsed)

        rltContainer?.visible()

        adapter = NewReleaseVideoAdapter(listOf(), this) {
            val mainActivity = requireActivity() as MainActivity
            mainActivity.collapseBottomPanel()
        }
        recyclerView.adapter = adapter

        viewModel.trendingTracks.observe(this, Observer { resource ->
            Picasso.get().load(resource.data?.get(0)?.imgUrl)
                .fit()
                .centerInside()
                .placeholder(R.drawable.bg_circle_black)
                .into(imgCollapsed)
            updateUI(resource)
        })

        viewModel.loadTrendingMusic()
    }

    private fun updateUI(resource: Resource<List<MusicTrack>>) {
        if (resource.status == Status.LOADING) {
            txtError.gone()
            progressBar.visible()
            recyclerView.gone()
        } else if (resource.status == Status.ERROR) {
            txtError.visible()
            progressBar.gone()
            recyclerView.gone()
        } else {
            txtError.gone()
            progressBar.gone()
            recyclerView.visible()
            adapter.items = resource.data!!
        }
    }

    override fun onItemClick(musicTrack: MusicTrack) {
        val bottomSheetFragment = FvaBottomSheetFragment()
        val bundle = Bundle()
        bundle.putString("MUSIC_TRACK", gson.toJson(musicTrack))
        bottomSheetFragment.arguments = bundle
        bottomSheetFragment.show(childFragmentManager, bottomSheetFragment.tag)
    }
}
