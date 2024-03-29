package com.cas.musicplayer.ui.common.songs

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.findFragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cas.common.extensions.activity
import com.cas.common.extensions.inflate
import com.cas.common.extensions.onClick
import com.cas.common.recyclerview.AlignLeftPagerSnapHelper
import com.cas.common.recyclerview.PercentGridLayoutManager
import com.cas.musicplayer.R
import com.cas.musicplayer.delegateadapter.AdapterDelegate
import com.cas.musicplayer.ui.bottomsheet.TrackOptionsFragment
import com.cas.musicplayer.ui.common.multiselection.MultiSelectTrackFragment
import com.cas.musicplayer.ui.home.delegates.HomeMarginProvider
import com.cas.musicplayer.utils.dpToPixel
import com.mousiki.shared.domain.models.DisplayableItem
import com.mousiki.shared.domain.models.DisplayedVideoItem
import com.mousiki.shared.domain.models.Track
import com.mousiki.shared.ui.home.model.HomeItem
import com.mousiki.shared.ui.resource.Resource

/**
 ***************************************
 * Created by Abdelhadi on 2019-12-04.
 ***************************************
 */

// Help needed: Meaningful name for this class
open class NewHorizontalSongsAdapterDelegate(
    private val onVideoSelected: (Track, List<Track>) -> Unit,
    private val onClickRetry: () -> Unit = {}
) : AdapterDelegate<List<DisplayableItem>>() {

    protected open val showRetryButton: Boolean = true

    override fun isForViewType(items: List<DisplayableItem>, position: Int): Boolean {
        return items[position] is HomeItem.PopularsItem
    }

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val view = parent.inflate(R.layout.horizontal_songs_list)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        items: List<DisplayableItem>,
        position: Int,
        holder: RecyclerView.ViewHolder
    ) {
        val songs = songsFromItem(items[position])
        val title = getHeaderTitle(items, position)
        (holder as ViewHolder).bind(title, songs, title.isNotEmpty())
    }

    override fun onBindViewHolder(
        items: List<DisplayableItem>,
        position: Int,
        holder: RecyclerView.ViewHolder,
        payloads: List<Any>
    ) {
        val item = items[position]
        val tracks = when (item) {
            is HomeItem.PopularsItem -> (item.resource as? Resource.Success)?.data
            is HomeItem.VideoList -> item.items
            else -> null
        }

        val viewHolder = holder as ViewHolder
        if (payloads.isEmpty() || payloads[0] !is Bundle) {
            val resource = when (item) {
                is HomeItem.PopularsItem -> item.resource
                else -> Resource.Success(tracks.orEmpty())
            }
            viewHolder.bind(
                getHeaderTitle(items, position),
                resource,
                item is HomeItem.VideoList
            )
        } else {
            viewHolder.update(tracks.orEmpty())
        }
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

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view),
        HomeMarginProvider {

        private var tracks: List<Track> = emptyList()

        private val adapter = SongsAdapter(
            onVideoSelected = { track ->
                onVideoSelected(track, tracks)
            },
            onClickMore = { track ->
                val fm = itemView.findFragment<Fragment>().childFragmentManager
                TrackOptionsFragment.present(fm, track)
            },
            onLongPressTrack = { track ->
                val activity = itemView.activity as? FragmentActivity ?: return@SongsAdapter
                MultiSelectTrackFragment.present(activity, tracks, track)
            }
        )
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
            recyclerView.layoutManager = PercentGridLayoutManager(itemView.context, 3)
            recyclerView.adapter = adapter
            AlignLeftPagerSnapHelper().attachToRecyclerView(recyclerView)
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
                    adjustSpanCount(resource.data.count())
                    adapter.submitList(resource.data)
                    viewError.isVisible = resource.data.isEmpty()
                    progressBar.isVisible = false
                    recyclerView.isInvisible = false

                    this.tracks = resource.data.map { it.track }
                }
                is Resource.Failure -> {
                    viewError.isVisible = true
                    progressBar.isVisible = false
                    recyclerView.isInvisible = true
                }
            }
        }

        private fun adjustSpanCount(itemsCount: Int) {
            val manager = recyclerView.layoutManager as? GridLayoutManager
            val spanCount = manager?.spanCount ?: 1
            if (itemsCount < DEF_SPAN_COUNT) {
                manager?.spanCount = if (itemsCount > 0) itemsCount else 1
            } else if (spanCount != DEF_SPAN_COUNT) {
                manager?.spanCount = DEF_SPAN_COUNT
            }
        }

        override fun topMargin(): Int {
            if (!txtTitle.isVisible) return 0
            return itemView.context.dpToPixel(24f)
        }

        fun update(items: List<DisplayedVideoItem>) {
            adapter.submitList(items)
            viewError.isVisible = items.isEmpty()
            progressBar.isVisible = false
            recyclerView.isInvisible = false

            this.tracks = items.map { it.track }
        }
    }

    companion object {
        private const val DEF_SPAN_COUNT = 3
    }
}
