package com.cas.musicplayer.ui.genres.list

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import com.cas.musicplayer.R
import com.cas.musicplayer.base.SimpleBaseAdapter
import com.cas.musicplayer.base.SimpleBaseViewHolder
import com.cas.musicplayer.ui.genres.detailgenre.DetailGenreFragment
import com.cas.musicplayer.ui.home.domain.model.GenreMusic
import com.cas.musicplayer.utils.dpToPixel
import com.cas.musicplayer.utils.drawable

/**
 * Created by Fayssel Yabahddou on 4/13/19.
 */
class GenresAdapter : SimpleBaseAdapter<GenreMusic, GenresViewHolder>() {
    override val cellResId: Int = R.layout.item_genre
    override fun createViewHolder(view: View) = GenresViewHolder(view)
}

class GenresViewHolder(itemView: View) : SimpleBaseViewHolder<GenreMusic>(itemView) {

    private val imgCategory: ImageView = itemView.findViewById(R.id.imgCategory)
    private val txtTitle: TextView = itemView.findViewById(R.id.txtTitle)
    private val cardView: View = itemView.findViewById(R.id.cardView)

    override fun bind(item: GenreMusic) {
        itemView.layoutParams = when (adapterPosition) {
            0 -> ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            else -> ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                itemView.context.dpToPixel(120f)
            )
        }

        imgCategory.setImageDrawable(itemView.context.drawable(item.img))
        txtTitle.text = item.title
        cardView.setOnClickListener {
            itemView.findNavController().navigate(
                R.id.detailGenreFragment, bundleOf(
                    DetailGenreFragment.EXTRAS_GENRE to item
                )
            )
        }
    }
}