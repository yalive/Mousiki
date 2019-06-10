package com.secureappinc.musicplayer.di

import com.secureappinc.musicplayer.ui.home.HomeFragment
import com.secureappinc.musicplayer.ui.home.HomeViewModel
import dagger.Component
import javax.inject.Singleton

/**
 ***************************************
 * Created by Abdelhadi on 2019-06-09.
 ***************************************
 */

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {

    fun inject(homeViewModel: HomeViewModel)

    fun inject(homeFragment: HomeFragment)

}