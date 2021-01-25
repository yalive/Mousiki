//
//  HomeItem.swift
//  Mousiki
//
//  Created by A.Yabahddou on 1/20/21.
//  Copyright © 2021 YabaSoft. All rights reserved.
//

import Foundation
import shared

enum HomeItem {
    case Trending(items: [DisplayedVideoItem])
    case VideoList(items: [DisplayedVideoItem], title: String)
    case GenreItem(genres: [GenreMusic])
    case ArtistItem(artists: [Artist])
    case SimplePlaylists(playlists: [SimplePlaylist], title: String)
    case CompactPlaylists(playlists: [CompactPlaylist], title: String)
    
    func title() -> String {
        switch self {
        case .Trending(items: _):
            return "Trending"
        case .VideoList(items: _, title: let title):
            return title
        case .GenreItem(genres: _):
            return "Genres"
        case .ArtistItem(artists: _):
            return "Artists"
        case .SimplePlaylists(playlists: _, let title):
            return title
        case .CompactPlaylists(playlists: _, let title):
            return title
        }
    }
}
