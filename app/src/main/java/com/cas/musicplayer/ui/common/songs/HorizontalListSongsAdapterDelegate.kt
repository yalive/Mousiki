package com.cas.musicplayer.ui.common.songs

import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.cas.common.extensions.inflate
import com.cas.common.extensions.onClick
import com.cas.common.resource.Resource
import com.cas.delegatedadapter.AdapterDelegate
import com.cas.delegatedadapter.DisplayableItem
import com.cas.musicplayer.R
import com.cas.musicplayer.domain.model.HomeItem
import com.cas.musicplayer.domain.model.MusicTrack
import com.cas.musicplayer.ui.home.delegates.HomeMarginProvider
import com.cas.musicplayer.ui.home.model.DisplayedVideoItem
import com.cas.musicplayer.utils.dpToPixel

/**
 ***************************************
 * Created by Abdelhadi on 2019-12-04.
 ***************************************
 */

open class HorizontalListSongsAdapterDelegate(
    private val onVideoSelected: (MusicTrack, List<MusicTrack>) -> Unit,
    private val onClickRetry: () -> Unit = {}
) : AdapterDelegate<List<DisplayableItem>>() {

    protected open val showRetryButton: Boolean = true

    override fun isForViewType(items: List<DisplayableItem>, position: Int): Boolean {
        return items[position] is HomeItem.PopularsItem
    }

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val view = parent.inflate(R.layout.horizontal_songs_list)
        return HorizontalSongsListViewHolder(view)
    }

    override fun onBindViewHolder(
        items: List<DisplayableItem>,
        position: Int,
        holder: RecyclerView.ViewHolder
    ) {
        val songs = songsFromItem(items[position])
        val title = getHeaderTitle(items, position)
        (holder as HorizontalSongsListViewHolder).bind(title, songs, title.isNotEmpty())
    }

    protected open fun songsFromItem(
        item: DisplayableItem
    ): Resource<List<DisplayedVideoItem>> {
        return (item as HomeItem.PopularsItem).resource
    }

    @StringRes
    protected open fun getEmptyMessage(): Int {
        return R.string.common_empty_song_list
    }

    protected open fun getHeaderTitle(items: List<DisplayableItem>, position: Int): String {
        return ""
    }

    inner class HorizontalSongsListViewHolder(view: View) : RecyclerView.ViewHolder(view),
        HomeMarginProvider {
        val adapter = HorizontalSongsAdapter(onVideoSelected)
        private val txtTitle = itemView.findViewById<TextView>(R.id.txtTitle)
        private val txtEmpty = itemView.findViewById<TextView>(R.id.txtEmpty)
        private val btnRetry = itemView.findViewById<ImageButton>(R.id.btnRetry)
        private val progressBar = itemView.findViewById<ProgressBar>(R.id.progressBar)
        private val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        private val viewError = view.findViewById<ViewGroup>(R.id.viewError)

        init {
            txtEmpty.text = itemView.context.getText(getEmptyMessage())
            btnRetry.isVisible = showRetryButton
            btnRetry.onClick {
                onClickRetry()
            }
            viewError.onClick {
                onClickRetry()
            }
        }

        init {
            recyclerView.adapter = adapter
        }

        fun bind(
            title: String,
            resource: Resource<List<DisplayedVideoItem>>,
            isTitleVisible: Boolean
        ) {
            txtTitle.apply {
                text = title
                isVisible = isTitleVisible
            }
            return when (resource) {
                is Resource.Loading -> {
                    viewError.isVisible = false
                    recyclerView.isInvisible = true
                    progressBar.isVisible = true
                }
                is Resource.Success -> {
                    adapter.dataItems = resource.data.toMutableList()
                    viewError.isVisible = resource.data.isEmpty()
                    progressBar.isVisible = false
                    recyclerView.isInvisible = false
                }
                is Resource.Failure -> {
                    viewError.isVisible = true
                    progressBar.isVisible = false
                    recyclerView.isInvisible = true
                }
            }
        }

        override fun topMargin(): Int {
            if (!txtTitle.isVisible) return 0
            return itemView.context.dpToPixel(24f)
        }
    }
}
