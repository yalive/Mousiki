package com.secureappinc.musicplayer.ui.artists

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.secureappinc.musicplayer.R
import com.secureappinc.musicplayer.data.models.Artist
import com.squareup.picasso.Picasso


/**
 **********************************
 * Created by Abdelhadi on 4/4/19.
 **********************************
 */
class ArtistsAdapter(items: List<Artist>, var onClickArtist: (artist: Artist) -> Unit) :
    RecyclerView.Adapter<ArtistsAdapter.ViewHolder>() {

    private val headPositionMap = HashMap<String, Int>()


    var items: List<Artist> = items
        set(value) {
            field = value
            fillHeadMap()
            notifyDataSetChanged()
        }

    private fun fillHeadMap() {
        for ((index, item) in items.withIndex()) {
            val firstLetter = item.name[0].toString()
            if (!headPositionMap.containsKey(firstLetter)) {
                headPositionMap[firstLetter] = index
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(com.secureappinc.musicplayer.R.layout.item_list_artist, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.item_list_artist
    }

    override fun getItemCount() = items.size


    fun getLetterPosition(letter: String): Int {
        return if (headPositionMap.containsKey(letter)) (headPositionMap[letter] as Int).toInt() else -1
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val imgSong: ImageView = view.findViewById(com.secureappinc.musicplayer.R.id.imgSong)
        private val txtTitle: TextView = view.findViewById(com.secureappinc.musicplayer.R.id.txtTitle)

        init {
            view.setOnClickListener {
                if (adapterPosition >= 0) {
                    onClickArtist(items[adapterPosition])
                }
            }
        }

        fun bind(item: Artist) {
            item.urlImage?.let { urlImage ->
                if (urlImage.isNotEmpty()) {
                    Picasso.get().load(urlImage)
                        .fit()
                        .into(imgSong)
                }
            }

            txtTitle.text = item.name
        }
    }

}