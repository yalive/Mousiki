package com.secureappinc.musicplayer.ui.artists

import android.os.Bundle
import androidx.recyclerview.widget.DiffUtil
import com.secureappinc.musicplayer.data.models.Artist


/**
 **********************************
 * Created by Abdelhadi on 4/20/19.
 **********************************
 */
class ArtistsDiffUtilCallback(var newList: List<Artist>, var oldList: List<Artist>) : DiffUtil.Callback() {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return newList[newItemPosition].channelId == oldList[oldItemPosition].channelId
    }

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        if (newList[newItemPosition].urlImage == oldList[oldItemPosition].urlImage) {
            return true
        }
        return false
    }

    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
        val newModel = newList[newItemPosition]
        val oldModel = oldList[oldItemPosition]

        val diff = Bundle()

        if (newModel.urlImage != (oldModel.urlImage)) {
            diff.putString("urlImage", newModel.urlImage);
        }

        if (diff.size() == 0) {
            return null;
        }

        return diff;
    }
}