//
//  HomeVC.swift
//  Mousiki
//
//  Created by A.Yabahddou on 1/20/21.
//  Copyright © 2021 YabaSoft. All rights reserved.
//

import UIKit
import shared

class HomeVC: UIViewController {
    
    @IBOutlet weak var tableView: UITableView!
    
    private var homeItems = [HomeItem]()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        
        let settingsProvider = SettingsProvider()
        let preferenceHelper = PreferencesHelper(provider: settingsProvider)
        preferenceHelper.setYtbApiKeys(keys: [
            "AIzaSyBqhHieeAMC79qmGm67df6vm8kMQoTpnog",
            "AIzaSyC40U4MYSqEjNnNp8c1389vU3g7kJ1WGCo",
            "AIzaSyCnNXLH_W3I8pe0PIsNrDZWb5S9OONZ9vQ",
            "AIzaSyAyV0mk3Gdx-LHG7np_1kSFLcPd62YWIqA"
        ])
        let client = MousikiApiImpl(preferencesHelper: preferenceHelper)
        client.trending(maxResults: 30, regionCode: "ma", pageToken: "") { (resource, error) in
            if error == nil {
                let items = resource?.items
                print("")
            }
            print("")
        }
        
        //print("\(type(of: errorRS))")
        //print("\(type(of: successRS))")
        
        //        if let data = (successRS as? ResultData)?.data {
        //            print(" data = \(data)")
        //        }
        //
        //        if let error = (errorRS as? ResultError)?.error {
        //            print(" error = \(error)")
        //        }
        
        //        repo.getHome { (response, error) in
        //            if let homeRS = response {
        //                let compactPlaylists = homeRS.compactPlaylists
        //                let compactPlaylistsItems = compactPlaylists.map { (compactPlaylist) in
        //                    HomeItem.CompactPlaylists(
        //                        playlists: compactPlaylist.playlists!,
        //                        title: compactPlaylist.title!)
        //                }
        //
        //                let simplePlaylists = homeRS.simplePlaylists
        //                let simplePlaylistsItems = simplePlaylists.map { (simplePlaylist) in
        //                    HomeItem.SimplePlaylists(
        //                        playlists: simplePlaylist.playlists!,
        //                        title: simplePlaylist.title!)
        //                }
        //
        //                let videoLists = homeRS.videoLists
        //                let videoListsItems = videoLists.map { (videoList) in
        //                    HomeItem.VideoList(
        //                        items: videoList.videos!.map { $0.video.toTrack().toDisplayedVideoItem() },
        //                        title: videoList.title!)
        //                }
        //
        //                self.homeItems.removeAll()
        //                self.homeItems.append(contentsOf: videoListsItems)
        //                self.homeItems.append(contentsOf: compactPlaylistsItems)
        //                self.homeItems.append(contentsOf: simplePlaylistsItems)
        //                self.homeItems.append(.GenreItem(genres: GenresRepository().loadGenres()))
        //                self.homeItems.append(.ArtistItem(artists: FakeHomeKt.fakeArtists()))
        //
        //                self.tableView.reloadData()
        //            }
        //        }
        
        // Fake
        //        let video = MusicTrack(
        //            youtubeId: "123",
        //            title: "Hamid El Kasri Hamid El Kasri Hamid El Kasri Hamid El Kasri",
        //            duration: "3:32"
        //        ).toDisplayedVideoItem()
        //
        //        let videos = [
        //            video,
        //            video,
        //            video,
        //            video,
        //            video,
        //            video,
        //            video,
        //            video,
        //            video,
        //            video
        //        ]
        //
        //
        //        let compactsPlaylists = FakeHomeKt.compactPlaylists()
        //
        //        // Playlist
        //        let simplePlaylists = FakeHomeKt.simplePlaylists()
        //
        //        let genres = GenresRepository().loadGenres()
        //        homeItems = [
        //            .VideoList(items: videos, title: "Trending"),
        //            .VideoList(items: videos, title: "New Release"),
        //            .CompactPlaylists(playlists: compactsPlaylists, title: "Today's Biggest Hits"),
        //            .CompactPlaylists(playlists: compactsPlaylists, title: "New & Trending Songs"),
        //            .CompactPlaylists(playlists: compactsPlaylists, title: "Fun Throwbacks"),
        //            .CompactPlaylists(playlists: compactsPlaylists, title: "Music for any Mood, Moment, or Vibe"),
        //            .VideoList(items: videos, title: "Latest videos"),
        //            .SimplePlaylists(playlists: simplePlaylists, title: "Top charts"),
        //            .GenreItem(genres: genres),
        //            .VideoList(items: videos, title: "Upcoming Premieres"),
        //            .VideoList(items: videos, title: "Black Lives Matter"),
        //            .VideoList(items: videos, title: "Classic Videos, Fresh Look"),
        //            .VideoList(items: videos, title: "Unique Performances"),
        //            .ArtistItem(artists: FakeHomeKt.fakeArtists())
        //        ]
        // End fake
        
        self.navigationController?.isNavigationBarHidden = true
        
        tableView.registerCellNib("VideoListCell",identifier: "VideoListCell")
        tableView.registerCellNib("SimplePlaylistsRow",identifier: "SimplePlaylistsRow")
        tableView.registerCellNib("CompactPlaylistsRow",identifier: "CompactPlaylistsRow")
        tableView.registerCellNib("HomeGenreCell",identifier: "HomeGenreCell")
        tableView.registerCellNib("HomeArtistRow",identifier: "HomeArtistRow")
    }
    
}

extension HomeVC: UITableViewDelegate, UITableViewDataSource {
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return 1
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        
        let homeItem = homeItems[indexPath.section]
        
        switch homeItem {
        case .Trending(items: _):
            break
        case .VideoList(items: let tracks, title: _):
            let cell = tableView.dequeueReusableCell(withIdentifier: "VideoListCell") as! VideoListCell
            cell.bind(items: tracks)
            return cell
        case .GenreItem(genres: let genres):
            let cell = tableView.dequeueReusableCell(withIdentifier: "HomeGenreCell") as! HomeGenreCell
            cell.bind(genres)
            return cell
        case .ArtistItem(artists: let artists):
            let cell = tableView.dequeueReusableCell(withIdentifier: "HomeArtistRow") as! HomeArtistRow
            cell.bind(items: artists)
            return cell
        case .SimplePlaylists(playlists: let playlists, title: _):
            let cell = tableView.dequeueReusableCell(withIdentifier: "SimplePlaylistsRow") as! SimplePlaylistsRow
            cell.bind(items: playlists)
            return cell
        case .CompactPlaylists(playlists: let playlists, title: _):
            let cell = tableView.dequeueReusableCell(withIdentifier: "CompactPlaylistsRow") as! CompactPlaylistsRow
            cell.bind(items: playlists)
            return cell
        }
        return UITableViewCell()
    }
    
    
    func numberOfSections(in tableView: UITableView) -> Int {
        homeItems.count
    }
    
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        let homeItem = homeItems[indexPath.section]
        switch homeItem {
        case .VideoList(items: _, title: _):
            return 200
        case .CompactPlaylists(playlists: _, title: _):
            return 240
        case .SimplePlaylists(playlists: _, title: _):
            return 220
        case .GenreItem(genres: _):
            return 460
        case .ArtistItem(artists: _):
            return 180 * 3 + 4
        default:
            return 200
        }
    }
    
    
    func tableView(_ tableView: UITableView, viewForHeaderInSection section: Int) -> UIView? {
        let item = homeItems[section]
        
        let rect = CGRect(x: 0, y: 0, width: tableView.frame.size.width, height: 60)
        let sectionView = HomeSectionView(frame:rect)
        
        sectionView.lblTitle.text = item.title()
        
        sectionView.actionBlock = {
            //self.showAllVideos(section)
        }
        return sectionView
    }
    
    func tableView(_ tableView: UITableView, heightForHeaderInSection section: Int) -> CGFloat {
        return 60
    }
}
