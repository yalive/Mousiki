package com.cas.musicplayer.ui.library.adapters

import com.cas.delegatedadapter.BaseDelegationAdapter
import com.cas.musicplayer.ui.library.LibraryViewModel
import com.cas.musicplayer.ui.library.delegates.LibraryCreatePlaylistDelegate
import com.cas.musicplayer.ui.library.delegates.LibraryCustomPlaylistDelegate

class LibraryPlaylistsAdapter(viewModel: LibraryViewModel) : BaseDelegationAdapter(
    listOf(
        LibraryCustomPlaylistDelegate(viewModel),
        LibraryCreatePlaylistDelegate(viewModel)
    )
)