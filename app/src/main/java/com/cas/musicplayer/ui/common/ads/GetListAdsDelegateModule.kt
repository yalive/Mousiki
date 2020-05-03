package com.cas.musicplayer.ui.common.ads

import dagger.Binds
import dagger.Module
import javax.inject.Singleton

/**
 ***************************************
 * Created by Y.Abdelhadi on 5/3/20.
 ***************************************
 */
@Module
interface GetListAdsDelegateModule {

    @Singleton
    @Binds
    fun bindsAdsDelegate(delegate: GetListAdsDelegateImp): GetListAdsDelegate
}