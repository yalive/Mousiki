package com.secureappinc.musicplayer.ui.detailcategory.playlists


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.secureappinc.musicplayer.R
import com.secureappinc.musicplayer.net.ApiManager
import com.secureappinc.musicplayer.ui.detailcategory.DetailGenreFragment
import com.secureappinc.musicplayer.ui.home.models.GenreMusic
import com.secureappinc.musicplayer.ui.home.uiScope
import com.secureappinc.musicplayer.utils.getCurrentLocale
import com.secureappinc.musicplayer.utils.gone
import com.secureappinc.musicplayer.utils.visible
import kotlinx.android.synthetic.main.fragment_genre_videos.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


class GenrePlaylistsFragment : Fragment(), CoroutineScope {

    val TAG = "DetailCategoryFragment"

    val job = Job()
    override val coroutineContext = job + Dispatchers.Main

    lateinit var adapter: GenrePlaylistsAdapter
    lateinit var genreMusic: GenreMusic

    private var isActive = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_genre_videos, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val parcelableGenre = arguments?.getParcelable<GenreMusic>(DetailGenreFragment.EXTRAS_GENRE)
        if (parcelableGenre == null) {
            requireActivity().onBackPressed()
            return
        }
        genreMusic = parcelableGenre
        adapter = GenrePlaylistsAdapter(listOf(), genreMusic)
        recyclerView.adapter = adapter
        loadPlaylist()
    }

    override fun onStart() {
        super.onStart()
        isActive = true
    }

    override fun onStop() {
        super.onStop()
        isActive = false
    }

    private fun loadPlaylist() {
        uiScope.launch(coroutineContext) {
            try {
                val response = ApiManager.api.getPlaylist(genreMusic.channelId, getCurrentLocale())
                showSuccess()
                val listMusics = response.items
                listMusics.let {
                    adapter.items = listMusics.filter { it.contentDetails.itemCount > 0 }
                }
            } catch (e: Exception) {
                showError()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        job.cancel() // TODO: Move to base class or use viewModel
    }

    fun showSuccess() {
        if (!isActive) return
        progressBar.gone()
        recyclerView.visible()
        txtError.gone()
    }

    fun showError() {
        if (!isActive) return
        progressBar.gone()
        recyclerView.gone()
        txtError.visible()
    }
}
