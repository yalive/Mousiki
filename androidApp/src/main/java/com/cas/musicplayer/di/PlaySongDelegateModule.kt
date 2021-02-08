package com.cas.musicplayer.di

import com.mousiki.shared.player.PlaySongDelegate
import com.cas.musicplayer.ui.common.PlaySongDelegateImpl
import org.koin.dsl.bind
import org.koin.dsl.module

val playSongDelegateModule = module {
    single { PlaySongDelegateImpl(get()) } bind PlaySongDelegate::class
}