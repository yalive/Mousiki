package com.cas.musicplayer.ui.home.adapters

import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.cas.common.delegate.observer
import com.cas.common.extensions.inflate
import com.cas.common.resource.Resource
import com.cas.common.resource.doOnSuccess
import com.cas.musicplayer.R
import com.cas.musicplayer.data.remote.models.Artist
import com.cas.musicplayer.domain.model.ChartModel
import com.cas.musicplayer.domain.model.GenreMusic
import com.cas.musicplayer.domain.model.HeaderItem
import com.cas.musicplayer.domain.model.HeaderItem.*
import com.cas.musicplayer.domain.model.HomeItem.*
import com.cas.musicplayer.ui.home.GridSpacingItemDecoration
import com.cas.musicplayer.ui.home.model.DisplayedVideoItem
import com.cas.musicplayer.utils.dpToPixel

/**
 **********************************
 * Created by Abdelhadi on 4/4/19.
 **********************************
 */
class HomeAdapter(
    private val onVideoSelected: () -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var recentTracks: List<DisplayedVideoItem> by observer(emptyList()) {
        featuredViewHolder?.bind()
    }

    private var newReleaseItems: List<DisplayedVideoItem> by observer(emptyList()) {
        popularSongsViewHolder?.bind()
    }
    private var charts: List<ChartModel> by observer(emptyList()) {
        chartViewHolder?.bind()
    }
    private var genres: List<GenreMusic> by observer(emptyList()) {
        genreViewHolder?.bind()
    }
    private var artists: List<Artist> by observer(emptyList()) {
        artistViewHolder?.bind()
    }

    private val items = listOf(
        RecentHeader,
        RecentItem,
        PopularsHeader,
        PopularsItem,
        ChartsHeader,
        ChartItem,
        ArtistsHeader,
        ArtistItem,
        GenresHeader,
        GenreItem
    )
    private var popularSongsViewHolder: PopularSongsViewHolder? = null
    private var featuredViewHolder: FeaturedViewHolder? = null
    private var chartViewHolder: ChartViewHolder? = null
    private var genreViewHolder: GenreViewHolder? = null
    private var artistViewHolder: ArtistViewHolder? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = when (viewType) {
        TYPE_HEADER -> HeaderViewHolder(
            parent.inflate(
                R.layout.item_home_header
            )
        )
        TYPE_FEATURED -> FeaturedViewHolder(parent.inflate(R.layout.item_home_new_release)).also {
            featuredViewHolder = it
        }
        TYPE_NEW_RELEASE -> PopularSongsViewHolder(parent.inflate(R.layout.item_home_new_release)).also {
            popularSongsViewHolder = it
        }
        TYPE_ARTIST -> ArtistViewHolder(parent.inflate(R.layout.item_home_list_artists)).also {
            artistViewHolder = it
        }
        TYPE_CHART -> ChartViewHolder(parent.inflate(R.layout.item_home_new_release)).also {
            chartViewHolder = it
        }
        else -> GenreViewHolder(parent.inflate(R.layout.item_home_list_genres)).also {
            genreViewHolder = it
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) = when (holder) {
        is FeaturedViewHolder -> holder.bind()
        is PopularSongsViewHolder -> holder.bind()
        is HeaderViewHolder -> holder.bind(items[position] as HeaderItem)
        is GenreViewHolder -> holder.bind()
        is ArtistViewHolder -> holder.bind()
        is ChartViewHolder -> holder.bind()
        else -> {
            // Nothing
        }
    }

    override fun getItemViewType(position: Int): Int = items[position].type
    override fun getItemCount() = items.size

    fun updateRecentSongs(songs: List<DisplayedVideoItem>) {
        this.recentTracks = songs
    }

    fun updateNewRelease(resource: Resource<List<DisplayedVideoItem>>) {
        resource.doOnSuccess {
            this.newReleaseItems = it
        }
    }

    fun updateCharts(charts: List<ChartModel>) {
        this.charts = charts
    }

    fun updateGenres(genres: List<GenreMusic>) {
        this.genres = genres
    }

    fun updateArtists(resource: Resource<List<Artist>>) {
        resource.doOnSuccess {
            this.artists = it
        }
    }

    inner class FeaturedViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private var adapter = HomeRecentPlayedSongsAdapter(onVideoSelected)

        init {
            val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
            recyclerView.adapter = adapter
        }

        fun bind() {
            val list: List<RecentPlayedSongItem> = recentTracks.map {
                RecentPlayedSongItem.RecentSong(it)
            }
            adapter.dataItems = list.toMutableList().apply {
                add(RecentPlayedSongItem.More)
            }
        }
    }

    inner class PopularSongsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private var adapter = HomePopularSongsAdapter(onVideoSelected)

        init {
            val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
            recyclerView.adapter = adapter
        }

        fun bind() {
            adapter.dataItems = newReleaseItems.toMutableList()
        }
    }

    inner class ChartViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private var adapter = HomeChartAdapter()

        init {
            val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)
            recyclerView.adapter = adapter
        }

        fun bind() {
            adapter.dataItems = charts.toMutableList()
        }
    }

    inner class ArtistViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private var adapter = HomeArtistsAdapter()

        init {
            val spacingDp = itemView.context.dpToPixel(8f)
            val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
            recyclerView.addItemDecoration(GridSpacingItemDecoration(3, spacingDp, true))
            recyclerView.adapter = adapter
        }

        fun bind() {
            adapter.dataItems = artists.toMutableList()
        }
    }

    inner class GenreViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private var adapter = HomeGenresAdapter()

        init {
            val spacingDp = itemView.context.dpToPixel(8f)
            val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
            recyclerView.addItemDecoration(GridSpacingItemDecoration(3, spacingDp, true))
            recyclerView.adapter = adapter
        }

        fun bind() {
            adapter.dataItems = genres.toMutableList()
        }
    }

    class HeaderViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        private val txtTitle: TextView = view.findViewById(R.id.txtTitle)
        private val showAll: ImageButton = view.findViewById(R.id.showAll)

        fun bind(headerItem: HeaderItem) {
            txtTitle.text = headerItem.title
            view.setOnClickListener { showMore(headerItem) }
            showAll.setOnClickListener { showMore(headerItem) }
        }

        private fun showMore(headerItem: HeaderItem) {
            val destination = when (headerItem) {
                ArtistsHeader -> R.id.artistsFragment
                PopularsHeader -> R.id.newReleaseFragment
                ChartsHeader -> R.id.chartsFragment
                GenresHeader -> R.id.genresFragment
                RecentHeader -> R.id.newReleaseFragment
            }
            itemView.findNavController().navigate(destination)
        }
    }

    companion object {
        const val TYPE_FEATURED = 1
        const val TYPE_NEW_RELEASE = 2
        const val TYPE_HEADER = 3
        const val TYPE_ARTIST = 4
        const val TYPE_GENRE = 5
        const val TYPE_CHART = 6
    }
}