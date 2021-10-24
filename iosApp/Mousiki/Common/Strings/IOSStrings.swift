//
//  IOSStrings.swift
//  Mousiki
//
//  Created by A.Yabahddou on 2/8/21.
//  Copyright © 2021 YabaSoft. All rights reserved.
//

import Foundation
import shared

class IOSStrings: Strings {
    
    func playlistCreated(playlistName: String) -> String {
        "Playlist \(playlistName) created"
    }
    
    func playlistExist(playlistName: String) -> String {
        "Playlist \(playlistName) already exist"
    }
    
    func trackAddedToPlaylist(playlistName: String) -> String {
        "Added to \(playlistName)"
    }
    
    
    /// Home
    lazy var artists: String = R.string.localizable.artists()
    lazy var genres: String = R.string.localizable.genres()
    lazy var titleNewRelease: String = R.string.localizable.newReleases()
    lazy var titleTopCharts: String = R.string.localizable.topCharts()
    
    /// Library
    lazy var emptyFavouriteList: String = "No fav"
    lazy var libraryFavourites: String = R.string.localizable.libraryFavourites()
    lazy var libraryHeavySongs: String = R.string.localizable.libraryHeavySongs()
    lazy var libraryRecent: String = R.string.localizable.libraryRecent()
    lazy var libraryTitlePlaylist: String = R.string.localizable.libraryPlaylists()

}
