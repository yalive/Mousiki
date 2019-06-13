package com.secureappinc.musicplayer.ui.artists.artistdetail


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.secureappinc.musicplayer.R
import com.secureappinc.musicplayer.data.models.Artist
import com.secureappinc.musicplayer.ui.artists.artistdetail.playlists.ArtistPlaylistsFragment
import com.secureappinc.musicplayer.ui.artists.artistdetail.videos.ArtistVideosFragment
import com.secureappinc.musicplayer.ui.genres.detailgenre.DetailGenreViewModel
import com.secureappinc.musicplayer.utils.visible
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.fragment_detail_genre.*


class ArtistFragment : Fragment() {

    val TAG = "DetailCategoryFragment"

    companion object {
        val EXTRAS_ARTIST = "artist"
    }

    lateinit var artist: Artist

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_detail_genre, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val parcelableGenre = arguments?.getParcelable<Artist>(EXTRAS_ARTIST)
        if (parcelableGenre == null) {
            requireActivity().onBackPressed()
            return
        }
        artist = parcelableGenre

        val collapsingToolbar = activity?.findViewById<CollapsingToolbarLayout>(R.id.collapsingToolbar)

        collapsingToolbar?.isTitleEnabled = true

        collapsingToolbar?.title = artist.name

        val rltContainer = activity?.findViewById<RelativeLayout>(R.id.rltContainer)

        val imgCollapsed = activity?.findViewById<CircleImageView>(R.id.imgCollapsed)

        rltContainer?.visible()

        val videosFragment = ArtistVideosFragment()
        videosFragment.arguments = arguments

        val playlistsFragment = ArtistPlaylistsFragment()
        playlistsFragment.arguments = arguments

        viewPager.adapter = ArtistPagerAdapter(childFragmentManager, listOf(videosFragment, playlistsFragment))
        tabLayout.setupWithViewPager(viewPager)

        val viewModel = ViewModelProviders.of(this).get(DetailGenreViewModel::class.java)

        artist.urlImage?.let { urlImage ->
            if (urlImage.isNotEmpty()) {
                Picasso.get().load(urlImage)
                    .fit()
                    .centerInside()
                    .placeholder(R.drawable.bg_circle_black)
                    .into(imgCollapsed)
            }
        }
    }
}
