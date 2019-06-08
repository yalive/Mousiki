package com.secureappinc.musicplayer.ui.artistdetail.playlists

import androidx.lifecycle.MutableLiveData
import com.secureappinc.musicplayer.base.BaseViewModel
import com.secureappinc.musicplayer.models.Resource
import com.secureappinc.musicplayer.models.YTTrendingItem
import com.secureappinc.musicplayer.ui.home.uiScope
import com.secureappinc.musicplayer.utils.getCurrentLocale
import kotlinx.coroutines.launch

/**
 **********************************
 * Created by Abdelhadi on 4/12/19.
 **********************************
 */
class ArtistPlaylistsViewModel : BaseViewModel() {

    val searchResultList = MutableLiveData<Resource<List<YTTrendingItem>>>()

    fun loadPlaylist(channelId: String) {
        val oldValue = searchResultList.value
        if (oldValue?.data != null && oldValue.data!!.isNotEmpty()) {
            return
        }
        searchResultList.value = Resource.loading()
        uiScope.launch(coroutineContext) {
            try {
                val response = api().getPlaylist(channelId, getCurrentLocale())
                searchResultList.value = Resource.success(response.items)
            } catch (e: Exception) {
                searchResultList.value = Resource.error("Error")
            }
        }
    }
}