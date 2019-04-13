package com.secureappinc.musicplayer.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.secureappinc.musicplayer.R
import com.secureappinc.musicplayer.models.enteties.MusicTrack
import com.secureappinc.musicplayer.ui.MainViewModel
import com.secureappinc.musicplayer.ui.home.models.GenreItem
import com.secureappinc.musicplayer.ui.home.models.GenreMusic
import com.secureappinc.musicplayer.ui.home.models.HeaderItem
import com.secureappinc.musicplayer.ui.home.models.HomeItem

/**
 **********************************
 * Created by Abdelhadi on 4/4/19.
 **********************************
 */
class HomeAdapter(
    val items: List<HomeItem>,
    val viewModel: MainViewModel,
    val callback: (item: HomeItem) -> Unit,
    val onVideoSelected: () -> Unit
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
            holder.bind(headerItem)
        } else if (holder is GenreViewHolder) {
            val genreItem = items[position] as GenreItem
            holder.bind(genreItem.genre)
        }
    }

    override fun getItemViewType(position: Int): Int {
        val item = items[position]
        return item.type
    }

    override fun getItemCount() = items.size

    inner class ArtistViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        init {
            view.setOnClickListener {
                callback(items[adapterPosition])
            }
        }

        fun bind() {

        }
    }

    inner class GenreViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val imgCategory: ImageView = view.findViewById(R.id.imgCategory)
        val txtTitle: TextView = view.findViewById(R.id.txtTitle)

        init {
            view.findViewById<ViewGroup>(R.id.cardView).setOnClickListener {
                callback(items[adapterPosition])
            }
        }

        fun bind(genreMusic: GenreMusic) {
            txtTitle.text = genreMusic.title
            imgCategory.setImageDrawable(ContextCompat.getDrawable(itemView.context, genreMusic.img))
        }
    }

    class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val txtTitle: TextView = view.findViewById(R.id.txtTitle)

        fun bind(headerItem: HeaderItem) {
            txtTitle.text = headerItem.title
        }
    }

    inner class FeaturedViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private var viewPager: ViewPager = view.findViewById(R.id.viewPager)
        private lateinit var adapter: HomeFeaturedAdapter

        fun bind() {
            adapter = HomeFeaturedAdapter(itemView.context)
            adapter.pages = this@HomeAdapter.tracks

            // Disable clip to padding
            viewPager.setClipToPadding(false);
            // set padding manually, the more you set the padding the more you see of prev & next page
            viewPager.setPadding(40, 0, 40, 0);
            // sets a margin b/w individual pages to ensure that there is a gap b/w them
            viewPager.pageMargin = 20

            viewPager.adapter = adapter
        }

        fun update(value: List<MusicTrack>) {
            this.adapter.pages = value
            this.adapter.notifyDataSetChanged()
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
}