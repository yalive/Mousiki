package com.cas.musicplayer.ui.genres.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.cas.musicplayer.R
import com.cas.musicplayer.ui.genres.detailgenre.DetailGenreFragment
import com.cas.musicplayer.ui.home.domain.model.GenreMusic
import com.cas.musicplayer.utils.dpToPixel

/**
 * Created by Fayssel Yabahddou on 4/13/19.
 */
class GenresAdapter(items: List<GenreMusic>) : RecyclerView.Adapter<GenresAdapter.ViewHolder>() {

    var items: List<GenreMusic> = items
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_genre, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {

        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items.get(position), position)
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val imgCategory: ImageView = itemView.findViewById(R.id.imgCategory)
        private val txtTitle: TextView = itemView.findViewById(R.id.txtTitle)
        private val cardView: View = itemView.findViewById(R.id.cardView)

        init {
            cardView.setOnClickListener {
                val bundle = Bundle()
                bundle.putParcelable(DetailGenreFragment.EXTRAS_GENRE, items[adapterPosition])
                itemView.findNavController().navigate(com.cas.musicplayer.R.id.detailGenreFragment, bundle)
            }
        }

        fun bind(item: GenreMusic, position: Int) {
            if (position == 0) {
                itemView.layoutParams =
                    ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            } else {
                val itemHeight = itemView.context.dpToPixel(120f)
                itemView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, itemHeight)
            }
            imgCategory.setImageDrawable(ContextCompat.getDrawable(itemView.context, item.img))
            txtTitle.text = item.title
        }
    }
}