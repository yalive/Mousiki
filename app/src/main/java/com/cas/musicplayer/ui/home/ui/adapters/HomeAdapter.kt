package com.cas.musicplayer.ui.home.ui.adapters

import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.cas.musicplayer.R
import com.cas.musicplayer.data.models.Artist
import com.cas.musicplayer.ui.home.GridSpacingItemDecoration
import com.cas.musicplayer.ui.home.domain.model.ChartModel
import com.cas.musicplayer.ui.home.domain.model.GenreMusic
import com.cas.musicplayer.ui.home.domain.model.HeaderItem
import com.cas.musicplayer.ui.home.domain.model.HomeItem
import com.cas.musicplayer.ui.home.ui.model.NewReleaseDisplayedItem
import com.cas.musicplayer.utils.Extensions.inflate
import com.cas.musicplayer.utils.dpToPixel
import com.cas.musicplayer.utils.observer

/**
 **********************************
 * Created by Abdelhadi on 4/4/19.
 **********************************
 */
class HomeAdapter(
    private val items: MutableList<HomeItem>,
    private val onVideoSelected: () -> Unit,
    private val moreItemClickListener: OnMoreItemClickListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var newReleaseItems: List<NewReleaseDisplayedItem> by observer(emptyList()) {
        newReleaseViewHolder?.bind()
        featuredViewHolder?.bind()
    }
    var charts: List<ChartModel> by observer(emptyList()) {
        chartViewHolder?.bind()
    }
    var genres: List<GenreMusic> by observer(emptyList()) {
        genreViewHolder?.bind()
    }
    var artists: List<Artist> by observer(emptyList()) {
        artistViewHolder?.bind()
    }

    fun autoScrollFeaturedVideos() {
        featuredViewHolder?.autoScrollFeaturedVideos()
    }

    private var newReleaseViewHolder: NewReleaseViewHolder? = null
    private var featuredViewHolder: FeaturedViewHolder? = null
    private var chartViewHolder: ChartViewHolder? = null
    private var genreViewHolder: GenreViewHolder? = null
    private var artistViewHolder: ArtistViewHolder? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = when (viewType) {
        TYPE_HEADER -> HeaderViewHolder(parent.inflate(R.layout.item_home_header))
        TYPE_FEATURED -> FeaturedViewHolder(parent.inflate(R.layout.item_home_featured)).also {
            featuredViewHolder = it
        }
        TYPE_NEW_RELEASE -> NewReleaseViewHolder(parent.inflate(R.layout.item_home_new_release)).also {
            newReleaseViewHolder = it
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
        is NewReleaseViewHolder -> holder.bind()
        is HeaderViewHolder -> {
            val headerItem = items[position] as HeaderItem
            holder.bind(headerItem, moreItemClickListener)
        }
        is GenreViewHolder -> holder.bind()
        is ArtistViewHolder -> holder.bind()
        is ChartViewHolder -> holder.bind()
        else -> {
            // Nothing
        }
    }

    override fun getItemViewType(position: Int): Int = items[position].type

    override fun getItemCount() = items.size

    inner class ArtistViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private var adapter = HomeArtistsAdapter()

        init {
            val spacingDp = itemView.context.dpToPixel(8f)
            val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
            recyclerView.addItemDecoration(GridSpacingItemDecoration(3, spacingDp, true))
            recyclerView.adapter = adapter
        }

        fun bind() {
            adapter.dataItems = artists
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
            adapter.dataItems = genres
        }
    }

    class HeaderViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        private val txtTitle: TextView = view.findViewById(R.id.txtTitle)
        private val showAll: ImageButton = view.findViewById(R.id.showAll)

        fun bind(headerItem: HeaderItem, moreItemClickListener: OnMoreItemClickListener) {
            txtTitle.text = headerItem.title
            view.setOnClickListener {
                moreItemClickListener.onMoreItemClick(headerItem)
            }

            showAll.setOnClickListener {
                moreItemClickListener.onMoreItemClick(headerItem)
            }
        }
    }

    inner class FeaturedViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val adapter = HomeFeaturedAdapter(view.context, onVideoSelected)
        private var viewPager: ViewPager = view.findViewById(R.id.viewPager)

        fun bind() {
            with(viewPager) {
                clipToPadding = false;
                setPadding(40, 0, 40, 0);
                pageMargin = 20
            }
            adapter.newReleaseItems = this@HomeAdapter.newReleaseItems
            viewPager.adapter = adapter
        }

        fun autoScrollFeaturedVideos() {
            val currentItem = viewPager.currentItem
            if (currentItem < adapter.newReleaseItems.size - 1) {
                viewPager.setCurrentItem(currentItem + 1, true)
            } else {
                viewPager.setCurrentItem(0, true)
            }
        }
    }

    inner class NewReleaseViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private var adapter = HomeNewReleaseAdapter(onVideoSelected)

        init {
            val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
            recyclerView.adapter = adapter
        }

        fun bind() {
            adapter.dataItems = newReleaseItems
        }
    }

    inner class ChartViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private var adapter = HomeChartAdapter()

        init {
            val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)
            recyclerView.adapter = adapter
        }

        fun bind() {
            adapter.dataItems = charts
        }
    }

    interface OnMoreItemClickListener {
        fun onMoreItemClick(headerItem: HeaderItem)
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