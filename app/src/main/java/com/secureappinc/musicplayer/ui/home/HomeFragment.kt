package com.secureappinc.musicplayer.ui.home


import android.content.Context
import android.os.Build
import android.os.Bundle
import android.telephony.TelephonyManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.secureappinc.musicplayer.R
import com.secureappinc.musicplayer.models.Artist
import com.secureappinc.musicplayer.models.Resource
import com.secureappinc.musicplayer.models.Status
import com.secureappinc.musicplayer.models.enteties.MusicTrack
import com.secureappinc.musicplayer.ui.MainActivity
import com.secureappinc.musicplayer.ui.MainViewModel
import com.secureappinc.musicplayer.ui.artistdetail.ArtistFragment
import com.secureappinc.musicplayer.ui.detailcategory.DetailGenreFragment
import com.secureappinc.musicplayer.ui.home.models.*
import com.secureappinc.musicplayer.utils.dpToPixel
import com.secureappinc.musicplayer.utils.gone
import com.secureappinc.musicplayer.utils.visible
import kotlinx.android.synthetic.main.fragment_home.*


class HomeFragment : Fragment(), HomeAdapter.onMoreItemClickListener {
    val TAG = "HomeFragment"

    lateinit var adapter: HomeAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(com.secureappinc.musicplayer.R.layout.fragment_home, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val gridLayoutManager = GridLayoutManager(requireContext(), 3)
        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                val viewType = adapter.getItemViewType(position)
                if (viewType == HomeAdapter.TYPE_GENRE || viewType == HomeAdapter.TYPE_ARTIST) {
                    return 1
                } else {
                    return 3
                }
            }
        }
        val collapsingToolbar = activity?.findViewById<CollapsingToolbarLayout>(R.id.collapsingToolbar)

        val rltContainer = activity?.findViewById<RelativeLayout>(R.id.rltContainer)

        rltContainer?.gone()

        collapsingToolbar?.isTitleEnabled = false

        adapter = HomeAdapter(mockList(), ViewModelProviders.of(requireActivity()).get(MainViewModel::class.java), {
            if (it is GenreItem) {
                val bundle = Bundle()
                bundle.putParcelable(DetailGenreFragment.EXTRAS_GENRE, it.genre)
                findNavController().navigate(com.secureappinc.musicplayer.R.id.detailGenreFragment, bundle)
            } else if (it is ArtistItem) {
                val bundle = Bundle()
                bundle.putParcelable(ArtistFragment.EXTRAS_ARTIST, it.artist)
                findNavController().navigate(com.secureappinc.musicplayer.R.id.artistFragment, bundle)
            }
        }, {
            val mainActivity = requireActivity() as MainActivity
            mainActivity.showBottomPanel()
        }, this)


        recyclerView.layoutManager = gridLayoutManager
        recyclerView.adapter = adapter
        val spacingDp = requireActivity().dpToPixel(8f)
        val marginDp = requireActivity().dpToPixel(8f)
        recyclerView.addItemDecoration(GridSpacingItemDecoration(spacingDp, marginDp))


        val viewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)
        viewModel.trendingTracks.observe(this, Observer { resource ->
            updateUI(resource)
        })

        viewModel.sixArtistResources.observe(this, Observer { resource ->
            updateArtists(resource)
        })

        viewModel.loadTrendingMusic()
        viewModel.loadArtists(getCurrentLocale())
    }

    private fun updateArtists(resource: Resource<List<Artist>>) {
        if (resource.status == Status.SUCCESS) {
            val artists = resource.data!!

            val artistItems = artists.map { ArtistItem(it) }

            if (artistItems.size == 6) {
                for (i in 4..9) {
                    adapter.items[i] = artistItems[i - 4]
                }

                adapter.notifyItemRangeChanged(4, 6)
            } else {
                // TODO
            }
        }
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
            adapter.tracks = resource.data!!
        }
    }


    fun mockList(): MutableList<HomeItem> {
        val list = mutableListOf<HomeItem>()
        for (i in 0 until 20) {
            if (i == 0) {
                list.add(FeaturedItem(listOf()))
            } else if (i == 1) {
                list.add(HeaderItem("New Release"))
            } else if (i == 2) {
                list.add(NewReleaseItem(listOf()))
            } else if (i == 3) {
                list.add(HeaderItem("ARTIST"))
            } else if (i in 4..9) {
                list.add(ArtistItem(artist = Artist("", "", "", "")))
            } else if (i == 10) {
                list.add(HeaderItem("GENRES"))
            } else if (i in 11..19) {
                list.add(GenreItem(GenreMusic.allValues[i - 11]))
            }
        }
        return list
    }

    override fun onMoreItemClick(headerItem: HeaderItem) {
        if (headerItem.title.equals("New Release")) {
            findNavController().navigate(com.secureappinc.musicplayer.R.id.newReleaseFragment)
        }
    }

    fun getCurrentLocale(): String {
        val tm = requireActivity().getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val countryCodeValue = tm.networkCountryIso

        if (countryCodeValue != null) {
            return countryCodeValue
        }

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            requireActivity().resources.configuration.locales.get(0).country
        } else {

            requireActivity().resources.configuration.locale.country
        }
    }
}
