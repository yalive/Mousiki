package com.secureappinc.musicplayer.ui.artistdetail


import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.secureappinc.musicplayer.R
import com.secureappinc.musicplayer.models.Artist
import com.secureappinc.musicplayer.ui.artistdetail.playlists.ArtistPlaylistsFragment
import com.secureappinc.musicplayer.ui.artistdetail.videos.ArtistVideosFragment
import com.secureappinc.musicplayer.ui.detailcategory.DetailGenreViewModel
import com.secureappinc.musicplayer.utils.BlurImage
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import kotlinx.android.synthetic.main.activity_main.*
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

        requireActivity().toolbar.title = artist.name

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
                    .into(imgProfileGenre)

                loadAndBlureImage(urlImage)
            }
        }
    }

    private fun loadAndBlureImage(urlImage: String) {
        val target = object : Target {
            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
            }

            override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
            }

            override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                blureBackgorundImg.setImageBitmap(BlurImage.fastblur(bitmap, 2f, 90))
            }
        }

        blureBackgorundImg.tag = target
        Picasso.get().load(urlImage).into(target)
    }
}
