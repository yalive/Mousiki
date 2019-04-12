package com.secureappinc.musicplayer.ui.detailcategory.videos

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.secureappinc.musicplayer.R
import com.secureappinc.musicplayer.models.YTCategoryMusictem
import com.secureappinc.musicplayer.ui.MainViewModel
import com.secureappinc.musicplayer.ui.home.models.GenreMusic
import com.squareup.picasso.Picasso

/**
 **********************************
 * Created by Abdelhadi on 4/4/19.
 **********************************
 */
class GenreVideosAdapter(items: List<YTCategoryMusictem>, val genreMusic: GenreMusic, val viewModel: MainViewModel) :
    RecyclerView.Adapter<GenreVideosAdapter.ViewHolder>() {

    var items: List<YTCategoryMusictem> = items
        set(value) {
            field = value
            notifyDataSetChanged()
        }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_category_video, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }


    override fun getItemCount() = items.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val imgSong: ImageView = view.findViewById(R.id.imgSong)
        private val txtTitle: TextView = view.findViewById(R.id.txtTitle)
        private val txtCategory: TextView = view.findViewById(R.id.txtCategory)

        init {
            view.setOnClickListener {
                viewModel.playVideo.value = items[adapterPosition].id.videoId
                viewModel.currentVideo.value = items[adapterPosition].snippet
            }
        }

        fun bind(item: YTCategoryMusictem) {
            Picasso.get().load("https://img.youtube.com/vi/${item.id.videoId}/maxresdefault.jpg")
                .fit()
                .into(imgSong)
            txtTitle.text = item.snippet.title
            txtCategory.text = "${genreMusic.title} - Topic"
        }
    }

}