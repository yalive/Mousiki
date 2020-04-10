package com.cas.musicplayer.ui.artists.list

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.cas.common.adapter.SimpleBaseAdapter
import com.cas.common.adapter.SimpleBaseViewHolder
import com.cas.musicplayer.R
import com.cas.musicplayer.data.remote.models.Artist
import com.cas.musicplayer.utils.loadImage


/**
 **********************************
 * Created by Abdelhadi on 4/4/19.
 **********************************
 */
class ArtistListAdapter(
    private val onClickArtist: (artist: Artist) -> Unit
) : SimpleBaseAdapter<Artist, ArtistListAdapter.ArtistsViewHolder>() {

    override val cellResId: Int = R.layout.item_list_artist
    private val headPositionMap = HashMap<String, Int>()

    override fun createViewHolder(view: View): ArtistsViewHolder {
        return ArtistsViewHolder(view, onClickArtist)
    }

    override fun onDataChanged() {
        fillHeadMap()
        notifyDataSetChanged()
    }

    private fun fillHeadMap() {
        for ((index, item) in dataItems.withIndex()) {
            val firstLetter = item.name[0].toString()
            if (!headPositionMap.containsKey(firstLetter)) {
                headPositionMap[firstLetter] = index
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.item_list_artist
    }

    fun getLetterPosition(letter: String): Int {
        return if (headPositionMap.containsKey(letter)) (headPositionMap[letter] as Int).toInt() else -1
    }

    inner class ArtistsViewHolder(
        view: View,
        var onClickArtist: (artist: Artist) -> Unit
    ) : SimpleBaseViewHolder<Artist>(view) {

        private val imgSong: ImageView = view.findViewById(R.id.imgSong)
        private val txtTitle: TextView = view.findViewById(R.id.txtTitle)

        override fun bind(item: Artist) {
            imgSong.loadImage(item.urlImage, placeHolder = null, errorImage = null)
            txtTitle.text = item.name
            itemView.setOnClickListener {
                if (adapterPosition >= 0) {
                    onClickArtist(item)
                }
            }
        }
    }
}