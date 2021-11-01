package com.cas.musicplayer.ui.library.adapters

import com.cas.musicplayer.delegateadapter.MousikiAdapter
import com.cas.musicplayer.ui.library.delegates.LibraryCreatePlaylistDelegate
import com.cas.musicplayer.ui.library.delegates.LibraryCustomPlaylistDelegate
import com.mousiki.shared.ui.library.LibraryViewModel

class LibraryPlaylistsAdapter(viewModel: LibraryViewModel) : MousikiAdapter(
    listOf(
        LibraryCustomPlaylistDelegate(viewModel),
        LibraryCreatePlaylistDelegate(viewModel)
    )
)