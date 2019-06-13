package com.secureappinc.musicplayer.di

import com.secureappinc.musicplayer.ui.artists.artistdetail.detailplaylist.PlaylistVideosFragment
import com.secureappinc.musicplayer.ui.artists.artistdetail.playlists.ArtistPlaylistsFragment
import com.secureappinc.musicplayer.ui.artists.artistdetail.videos.ArtistVideosFragment
import com.secureappinc.musicplayer.ui.artists.list.ArtistListFragment
import com.secureappinc.musicplayer.ui.bottompanel.PlayerBottomSheetFragment
import com.secureappinc.musicplayer.ui.bottomsheet.FvaBottomSheetFragment
import com.secureappinc.musicplayer.ui.charts.ChartsFragment
import com.secureappinc.musicplayer.ui.genres.detailgenre.playlists.GenrePlaylistsFragment
import com.secureappinc.musicplayer.ui.genres.detailgenre.videos.GenreVideosFragment
import com.secureappinc.musicplayer.ui.home.HomeFragment
import com.secureappinc.musicplayer.ui.newrelease.NewReleaseFragment
import com.secureappinc.musicplayer.ui.searchyoutube.SearchYoutubeFragment
import com.secureappinc.musicplayer.ui.searchyoutube.videos.YTSearchVideosFragment
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

    fun inject(homeFragment: HomeFragment)

    fun inject(newReleaseFragment: NewReleaseFragment)

    fun inject(genreVideosFragment: GenreVideosFragment)

    fun inject(genrePlaylistsFragment: GenrePlaylistsFragment)

    fun inject(artistPlaylistsFragment: ArtistPlaylistsFragment)

    fun inject(artistListFragment: ArtistListFragment)

    fun inject(artistVideosFragment: ArtistVideosFragment)

    fun inject(playlistVideosFragment: PlaylistVideosFragment)

    fun inject(searchYoutubeFragment: SearchYoutubeFragment)

    fun inject(chartsFragment: ChartsFragment)

    fun inject(playerBottomSheetFragment: PlayerBottomSheetFragment)

    fun inject(fvaBottomSheetFragment: FvaBottomSheetFragment)

    fun inject(ytSearchVideosFragment: YTSearchVideosFragment)

}