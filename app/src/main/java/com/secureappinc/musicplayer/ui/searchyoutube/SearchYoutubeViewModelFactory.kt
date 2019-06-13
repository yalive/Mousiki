package com.secureappinc.musicplayer.ui.searchyoutube

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.secureappinc.musicplayer.repository.SearchRepository
import javax.inject.Inject

/**
 ***************************************
 * Created by Abdelhadi on 2019-06-12.
 ***************************************
 */

class SearchYoutubeViewModelFactory @Inject constructor(val searchRepository: SearchRepository) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SearchYoutubeViewModel(searchRepository) as T
    }
}