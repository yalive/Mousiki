package com.secureappinc.musicplayer.ui.newrelease

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.secureappinc.musicplayer.repository.NewReleaseRepository
import javax.inject.Inject

/**
 ***************************************
 * Created by Abdelhadi on 2019-06-10.
 ***************************************
 */

class NewReleaseViewModelFactory @Inject constructor(val newReleaseRepository: NewReleaseRepository) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return NewReleaseViewModel(newReleaseRepository) as T
    }
}