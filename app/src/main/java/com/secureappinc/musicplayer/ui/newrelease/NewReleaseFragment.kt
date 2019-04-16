package com.secureappinc.musicplayer.ui.newrelease


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.gson.Gson
import com.secureappinc.musicplayer.R
import com.secureappinc.musicplayer.models.Resource
import com.secureappinc.musicplayer.models.Status
import com.secureappinc.musicplayer.models.enteties.MusicTrack
import com.secureappinc.musicplayer.ui.MainActivity
import com.secureappinc.musicplayer.ui.bottomsheet.FvaBottomSheetFragment
import com.secureappinc.musicplayer.utils.gone
import com.secureappinc.musicplayer.utils.visible
import kotlinx.android.synthetic.main.fragment_new_release.*

class NewReleaseFragment : Fragment(), NewReleaseVideoAdapter.onItemClickListener {

    val TAG = "NewReleaseFragment"

    lateinit var adapter: NewReleaseVideoAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new_release, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity!!.title = "New Release"

        adapter = NewReleaseVideoAdapter(listOf(), this) {
            val mainActivity = requireActivity() as MainActivity
            mainActivity.showBottomPanel()
        }

        recyclerView.adapter = adapter

        val viewModel = ViewModelProviders.of(this).get(NewReleaseViewModel::class.java)
        viewModel.trendingTracks.observe(this, Observer { resource ->
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
        bundle.putString("MUSIC_TRACK", Gson().toJson(musicTrack))
        bottomSheetFragment.arguments = bundle
        bottomSheetFragment.show(childFragmentManager, bottomSheetFragment.tag)
    }
}
