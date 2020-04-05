package com.cas.musicplayer.ui.playlist.create

import android.content.Context
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.GestureDetectorCompat
import com.cas.common.adapter.SimpleBaseAdapter
import com.cas.common.adapter.SimpleBaseViewHolder
import com.cas.common.extensions.scaleDown
import com.cas.common.extensions.scaleOriginal
import com.cas.musicplayer.R
import com.cas.musicplayer.domain.model.Playlist
import com.cas.musicplayer.utils.loadImage

/**
 ***************************************
 * Created by Y.Abdelhadi on 4/4/20.
 ***************************************
 */
class SelectPlaylistAdapter(
    private val context: Context,
    private val viewModel: AddTrackToPlaylistViewModel,
    override val cellResId: Int = R.layout.item_select_custom_playlist
) : SimpleBaseAdapter<Playlist, SelectPlaylistAdapter.ViewHolder>() {

    private val gestureDetector = ClickGestureListener { position ->
        viewModel.addTrackToPlaylist(position)
    }
    private val gestureDetectorCompat = GestureDetectorCompat(context, gestureDetector)

    override fun createViewHolder(view: View): ViewHolder {
        return ViewHolder(view)
    }

    inner class ViewHolder(view: View) : SimpleBaseViewHolder<Playlist>(view) {
        private val txtTitle = view.findViewById<TextView>(R.id.txtTitle)
        private val txtTracksCount = view.findViewById<TextView>(R.id.txtTracksCount)
        private val imgPlaylist = view.findViewById<ImageView>(R.id.imgPlaylist)

        init {
            itemView.setOnTouchListener { v, event ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                    v.scaleDown(to = 0.99f)
                    v.alpha = 0.80f
                } else if (event.action != MotionEvent.ACTION_MOVE) {
                    v.scaleOriginal()
                    v.alpha = 1f
                }
                gestureDetector.position = adapterPosition
                gestureDetectorCompat.onTouchEvent(event)
                return@setOnTouchListener true
            }
        }

        override fun bind(data: Playlist) {
            txtTitle.text = data.title
            txtTracksCount.text =
                context.resources.getQuantityString(
                    R.plurals.playlist_tracks_counts,
                    data.itemCount,
                    data.itemCount
                )
            val urlImage = data.urlImage
            imgPlaylist.loadImage(urlImage)
        }

    }
}

private class ClickGestureListener(
    val onClick: (Int) -> Unit
) : GestureDetector.SimpleOnGestureListener() {
    var position = -1
    override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
        onClick(position)
        return super.onSingleTapConfirmed(e)
    }
}