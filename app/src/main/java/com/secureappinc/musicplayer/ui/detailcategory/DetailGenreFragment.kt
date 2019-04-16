package com.secureappinc.musicplayer.ui.detailcategory


import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.secureappinc.musicplayer.R
import com.secureappinc.musicplayer.models.enteties.MusicTrack
import com.secureappinc.musicplayer.ui.detailcategory.playlists.GenrePlaylistsFragment
import com.secureappinc.musicplayer.ui.detailcategory.videos.GenreVideosFragment
import com.secureappinc.musicplayer.ui.home.models.GenreMusic
import com.secureappinc.musicplayer.utils.BlurImage
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_detail_genre.*


class DetailGenreFragment : Fragment() {

    val TAG = "DetailCategoryFragment"

    companion object {
        val EXTRAS_GENRE = "genre"
    }

    lateinit var genreMusic: GenreMusic

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_detail_genre, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val parcelableGenre = arguments?.getParcelable<GenreMusic>(EXTRAS_GENRE)
        if (parcelableGenre == null) {
            requireActivity().onBackPressed()
            return
        }
        genreMusic = parcelableGenre

        requireActivity().toolbar.title = genreMusic.title

        val videosFragment = GenreVideosFragment()
        videosFragment.arguments = arguments

        val playlistsFragment = GenrePlaylistsFragment()
        playlistsFragment.arguments = arguments

        viewPager.adapter = DetailGenrePagerAdapter(childFragmentManager, listOf(videosFragment, playlistsFragment))
        tabLayout.setupWithViewPager(viewPager)

        val viewModel = ViewModelProviders.of(this).get(DetailGenreViewModel::class.java)
        viewModel.firstTrack.observe(this, Observer { firstTrack ->
            /*Picasso.get().load(firstTrack.imgUrl)
                //.fit()
                .into(imgProfileGenre)*/
            loadAndBlureImage(firstTrack)
        })

        imgProfileGenre.setImageResource(genreMusic.img)
    }

    private fun loadAndBlureImage(video: MusicTrack) {
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
        Picasso.get().load(video.imgUrl).into(target)
    }
}
