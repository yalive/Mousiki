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
    
    fileprivate var viewModel: HomeVM!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        self.navigationController?.isNavigationBarHidden = true
        
        tableView.registerCellNib("VideoListCell",identifier: "VideoListCell")
        tableView.registerCellNib("SimplePlaylistsRow",identifier: "SimplePlaylistsRow")
        tableView.registerCellNib("CompactPlaylistsRow",identifier: "CompactPlaylistsRow")
        tableView.registerCellNib("HomeGenreCell",identifier: "HomeGenreCell")
        tableView.registerCellNib("HomeArtistRow",identifier: "HomeArtistRow")
        
        viewModel = HomeVM(delegate: self)
        
        
        DispatchQueue.main.asyncAfter(deadline: .now() + 5) {
            self.viewModel.loadArtists()
        }
        
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

extension HomeVC: HomeVMDelegate {
    
    func showHome(_ homeRS: HomeRS) {
        let compactPlaylists = homeRS.compactPlaylists
        let compactPlaylistsItems = compactPlaylists.map { (compactPlaylist) in
            HomeItem.CompactPlaylists(
                playlists: compactPlaylist.playlists!,
                title: compactPlaylist.title!)
        }
        
        let simplePlaylists = homeRS.simplePlaylists
        let simplePlaylistsItems = simplePlaylists.map { (simplePlaylist) in
            HomeItem.SimplePlaylists(
                playlists: simplePlaylist.playlists!,
                title: simplePlaylist.title!)
        }
        
        let videoLists = homeRS.videoLists
        let videoListsItems = videoLists.map { (videoList) in
            HomeItem.VideoList(
                items: videoList.videos!.map { $0.video.toTrack().toDisplayedVideoItem() },
                title: videoList.title!)
        }
        
        self.homeItems.removeAll()
        self.homeItems.append(contentsOf: videoListsItems)
        self.homeItems.append(contentsOf: compactPlaylistsItems)
        self.homeItems.append(contentsOf: simplePlaylistsItems)
        self.homeItems.append(.GenreItem(genres: GenresRepository().loadGenres()))
        
        self.tableView.reloadData()
    }
    
    func showTrending(_ items: [DisplayedVideoItem]) {
        let trendingItem = HomeItem.VideoList(items: items, title: "Trending")
        if self.homeItems.count > 1 {
            self.homeItems.insert(trendingItem, at: 1)
        } else {
            self.homeItems.insert(trendingItem, at: 0)
        }
        self.tableView.reloadData()
    }
    
    func showArtists(_ artists: [Artist]) {
        //self.homeItems.insert(.ArtistItem(artists: artists), at: 1)
        //self.tableView.reloadData()
    }
    
}

