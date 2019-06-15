package com.secureappinc.musicplayer.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.secureappinc.musicplayer.R
import com.secureappinc.musicplayer.data.enteties.MusicTrack
import com.secureappinc.musicplayer.data.models.Artist
import com.secureappinc.musicplayer.ui.home.models.*
import com.secureappinc.musicplayer.utils.AdsOrigin
import com.secureappinc.musicplayer.utils.RequestAdsLiveData
import com.secureappinc.musicplayer.utils.Utils
import com.squareup.picasso.Picasso

/**
 **********************************
 * Created by Abdelhadi on 4/4/19.
 **********************************
 */
class HomeAdapter(
    val items: MutableList<HomeItem>,
    val callback: (item: HomeItem) -> Unit,
    val onVideoSelected: () -> Unit,
    val moreItemClickListener: onMoreItemClickListener
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var newReleaseViewHolder: NewReleaseViewHolder? = null
    var featuredViewHolder: FeaturedViewHolder? = null

    var tracks: List<MusicTrack> = listOf()
        set(value) {
            field = value
            newReleaseViewHolder?.adapter?.updateList(value)
            featuredViewHolder?.update(value)

        }

    companion object {
        const val TYPE_FEATURED = 1
        const val TYPE_NEW_RELEASE = 2
        const val TYPE_HEADER = 3
        const val TYPE_ARTIST = 4
        const val TYPE_GENRE = 5
        const val TYPE_CHART = 6
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            TYPE_HEADER -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_home_header, parent, false)
                return HeaderViewHolder(view)
            }
            TYPE_FEATURED -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_home_featured, parent, false)
                featuredViewHolder = FeaturedViewHolder(view)
                return featuredViewHolder!!
            }
            TYPE_NEW_RELEASE -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_home_new_release, parent, false)
                newReleaseViewHolder = NewReleaseViewHolder(view)
                return newReleaseViewHolder!!
            }
            TYPE_ARTIST -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_home_artist, parent, false)
                return ArtistViewHolder(view)
            }
            TYPE_CHART -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_home_new_release, parent, false)
                return ChartViewHolder(view)
            }
            else -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_home_genre, parent, false)
                return GenreViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is FeaturedViewHolder) {
            holder.bind()
        } else if (holder is NewReleaseViewHolder) {
            holder.bind()
        } else if (holder is HeaderViewHolder) {
            val headerItem = items[position] as HeaderItem
            holder.bind(headerItem, moreItemClickListener)
        } else if (holder is GenreViewHolder) {
            val genreItem = items[position] as GenreItem
            holder.bind(genreItem.genre)
        } else if (holder is ArtistViewHolder) {
            val genreItem = items[position] as ArtistItem
            holder.bind(genreItem.artist)
        } else if (holder is ChartViewHolder) {
            val chartItem = items[position] as ChartItem

            holder.bind(chartItem.chartItems)
        }
    }

    override fun getItemViewType(position: Int): Int {
        val item = items[position]
        return item.type
    }

    override fun getItemCount() = items.size

    fun autoScrollFeaturedVideos() {
        featuredViewHolder?.autoScrollFeaturedVideos()
    }

    inner class ArtistViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val imgArtist: ImageView = view.findViewById(R.id.imgArtist)
        val txtName: TextView = view.findViewById(R.id.txtName)

        init {
            view.findViewById<CardView>(R.id.cardView).setOnClickListener {
                callback(items[adapterPosition])

                if (!Utils.hasShownAdsOneTime) {
                    Utils.hasShownAdsOneTime = true
                    RequestAdsLiveData.value = AdsOrigin("artist")
                }
            }
        }

        fun bind(artist: Artist) {
            txtName.text = artist.name
            if (artist.urlImage != null && artist.urlImage!!.isNotEmpty()) {
                Picasso.get().load(artist.urlImage)
                    .fit()
                    .into(imgArtist)
            }
        }
    }

    inner class GenreViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val imgCategory: ImageView = view.findViewById(R.id.imgCategory)
        val txtTitle: TextView = view.findViewById(R.id.txtTitle)

        init {
            view.findViewById<ViewGroup>(R.id.cardView).setOnClickListener {
                callback(items[adapterPosition])

                if (!Utils.hasShownAdsOneTime) {
                    Utils.hasShownAdsOneTime = true
                    RequestAdsLiveData.value = AdsOrigin("genre")
                }
            }
        }

        fun bind(genreMusic: GenreMusic) {
            txtTitle.text = genreMusic.title
            imgCategory.setImageDrawable(ContextCompat.getDrawable(itemView.context, genreMusic.img))
        }
    }

    class HeaderViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        val txtTitle: TextView = view.findViewById(R.id.txtTitle)
        val showAll: ImageButton = view.findViewById(R.id.showAll)

        fun bind(headerItem: HeaderItem, moreItemClickListener: onMoreItemClickListener) {
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

        private var viewPager: ViewPager = view.findViewById(R.id.viewPager)
        private lateinit var adapter: HomeFeaturedAdapter

        fun bind() {
            adapter = HomeFeaturedAdapter(itemView.context, onVideoSelected)
            adapter.tracks = this@HomeAdapter.tracks

            // Disable clip to padding
            viewPager.setClipToPadding(false);
            // set padding manually, the more you set the padding the more you see of prev & next page
            viewPager.setPadding(40, 0, 40, 0);
            // sets a margin b/w individual pages to ensure that there is a gap b/w them
            viewPager.pageMargin = 20

            viewPager.adapter = adapter
        }

        fun update(featuredTracks: List<MusicTrack>) {
            this.adapter.tracks = featuredTracks
            this.adapter.notifyDataSetChanged()
        }

        fun autoScrollFeaturedVideos() {
            val currentItem = viewPager.currentItem
            if (currentItem < adapter.tracks.size - 1) {
                viewPager.setCurrentItem(currentItem + 1, true)
            } else {
                viewPager.setCurrentItem(0, true)
            }
        }
    }

    inner class NewReleaseViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private var recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)
        lateinit var adapter: HomeNewReleaseAdapter

        fun bind() {
            adapter = HomeNewReleaseAdapter(this@HomeAdapter.tracks, onVideoSelected)
            recyclerView.layoutManager = LinearLayoutManager(itemView.context, RecyclerView.HORIZONTAL, false)
            recyclerView.adapter = adapter
        }

    }

    inner class ChartViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private var recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)
        lateinit var adapter: HomeChartAdapter

        fun bind(chartItems: List<ChartModel>) {
            adapter = HomeChartAdapter(chartItems)
            recyclerView.layoutManager = LinearLayoutManager(itemView.context, RecyclerView.HORIZONTAL, false)
            recyclerView.adapter = adapter
        }

    }

    interface onMoreItemClickListener {
        fun onMoreItemClick(headerItem: HeaderItem)
    }
}