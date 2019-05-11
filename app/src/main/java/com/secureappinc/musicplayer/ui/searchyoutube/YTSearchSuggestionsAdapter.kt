package com.secureappinc.musicplayer.ui.searchyoutube

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_youtube_serach_suggestion.view.*


/**
 **********************************
 * Created by Abdelhadi on 4/4/19.
 **********************************
 */
class YTSearchSuggestionsAdapter(suggestions: List<String>) :
    RecyclerView.Adapter<YTSearchSuggestionsAdapter.ViewHolder>() {

    var onClickItem: ((suggestion: String) -> Unit)? = null

    var suggestions: List<String> = suggestions
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(com.secureappinc.musicplayer.R.layout.item_youtube_serach_suggestion, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(suggestions[position])
    }


    override fun getItemCount() = suggestions.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        init {
            view.setOnClickListener {
                if (adapterPosition >= 0) {
                    onClickItem?.invoke(suggestions[adapterPosition])
                }
            }
        }

        fun bind(item: String) {
            itemView.txtTitle.text = item
        }
    }

}