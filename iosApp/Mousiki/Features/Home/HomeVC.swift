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
    
    fileprivate lazy var viewModel: HomeViewModel = {
        return Injector().homeViewModel
    }()
    
    fileprivate lazy var strings = Injector().strings
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.navigationController?.isNavigationBarHidden = true
        tableView.registerCellNib("VideoListCell", identifier: "VideoListCell")
        tableView.registerCellNib("SimplePlaylistsRow", identifier: "SimplePlaylistsRow")
        tableView.registerCellNib("CompactPlaylistsRow", identifier: "CompactPlaylistsRow")
        tableView.registerCellNib("HomeGenreCell", identifier: "HomeGenreCell")
        tableView.registerCellNib("HomeArtistRow", identifier: "HomeArtistRow")
        observeViewModel()
        
        let vm = Injector().searchViewModel
        vm.observeVideos {
            print("searchViewModel: OnError")
        } onLoading: {
            print("searchViewModel: onLoading")
        } onSuccess: { items in
            print("searchViewModel: got videos [\(items.count)]")
        }

        //vm.search(query: "Hamid el kasri")
        vm.getSuggestions(keyword: "adel")
        vm.observeSuggestions { suggestions in
            print("\(suggestions)")
        }
    }
    
    override func viewWillAppear(_ animated: Bool) {
        self.navigationController?.isNavigationBarHidden = true
    }
    
    func observeViewModel() {

        viewModel.homeItemsFlow.watch { items in
            guard let items = items else { return }
            let dispItems = items as! [DisplayableItem]
            
            let homeItems = dispItems.filter { item in
                !(item is MousikiTopBarItem)
            } as! [HomeItem]
            
            
            let filtredItems = homeItems.filter {
                !($0 is HeaderItem.PopularsHeader || $0 is HeaderItem.ArtistsHeader || $0 is HeaderItem.GenresHeader)
            }
            
            self.homeItems.removeAll()
            self.homeItems.append(contentsOf: filtredItems)
            self.tableView.reloadData()
        }
    }
}

extension HomeVC: UITableViewDelegate, UITableViewDataSource {
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return 1
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        
        let homeItem = homeItems[indexPath.section]
        
        if homeItem is HomeItem.PopularsItem {
            let resource = (homeItem as! HomeItem.PopularsItem).resource
            if resource is ResourceSuccess {
                let trendingItems = (resource as! ResourceSuccess).data as! [DisplayedVideoItem]
                let cell = tableView.dequeueReusableCell(withIdentifier: "VideoListCell") as! VideoListCell
                cell.delegate = self
                cell.bind(items: trendingItems)
                return cell
            }
            
        } else if homeItem is HomeItem.ArtistItem {
            let artistItem = homeItem as! HomeItem.ArtistItem
            let cell = tableView.dequeueReusableCell(withIdentifier: "HomeArtistRow") as! HomeArtistRow
            cell.delegate = self
            cell.bind(items: artistItem.artists)
            return cell
            
        } else if homeItem is HomeItem.GenreItem {
            let genreItem = homeItem as! HomeItem.GenreItem
            let cell = tableView.dequeueReusableCell(withIdentifier: "HomeGenreCell") as! HomeGenreCell
            cell.delegate = self
            cell.bind(genreItem.genres)
            return cell
        } else if homeItem is HomeItem.CompactPlaylists {
            let compactPlaylistItem = homeItem as! HomeItem.CompactPlaylists
            let cell = tableView.dequeueReusableCell(withIdentifier: "CompactPlaylistsRow") as! CompactPlaylistsRow
            cell.delegate = self
            cell.bind(items: compactPlaylistItem.playlists)
            return cell
        } else if homeItem is HomeItem.SimplePlaylists {
            let simplePlaylistItem = homeItem as! HomeItem.SimplePlaylists
            let cell = tableView.dequeueReusableCell(withIdentifier: "SimplePlaylistsRow") as! SimplePlaylistsRow
            cell.delegate = self
            cell.bind(items: simplePlaylistItem.playlists)
            return cell
        } else if homeItem is HomeItem.VideoList {
            let videoListItem = homeItem as! HomeItem.VideoList
            let cell = tableView.dequeueReusableCell(withIdentifier: "VideoListCell") as! VideoListCell
            cell.delegate = self
            cell.bind(items: videoListItem.items)
            return cell
        }
        return UITableViewCell()
    }
    
    
    func numberOfSections(in tableView: UITableView) -> Int {
        homeItems.count
    }
    
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        let homeItem = homeItems[indexPath.section]
        
        if homeItem is HomeItem.PopularsItem {
            return 200
        } else if homeItem is HomeItem.ArtistItem {
            return 180 * 3 + 4
        } else if homeItem is HomeItem.GenreItem {
            return 460
        } else if homeItem is HomeItem.CompactPlaylists {
            return 240
        } else if homeItem is HomeItem.SimplePlaylists {
            return 220
        } else if homeItem is HomeItem.VideoList {
            return 200
        } else {
            return 200
        }
    }
    
    
    func tableView(_ tableView: UITableView, viewForHeaderInSection section: Int) -> UIView? {
        let item = homeItems[section]
        
        let rect = CGRect(x: 0, y: 0, width: tableView.frame.size.width, height: 60)
        let sectionView = HomeSectionView(frame: rect)
        
        sectionView.lblTitle.text = item.title(strings: strings)
        
        sectionView.actionBlock = {
            //self.showAllVideos(section)
        }
        return sectionView
    }
    
    func tableView(_ tableView: UITableView, heightForHeaderInSection section: Int) -> CGFloat {
        return 60
    }
}

extension HomeVC: HomeGenreCellDelegate {
    
    func didTapGenre(_ genre: GenreMusic) {
        let vc = PlaylistSongsVC.loadFromNib()
        vc.playlistId = genre.topTracksPlaylist
        self.navigationController?.pushViewController(vc, animated: true)
    }
}

extension HomeVC: CompactPlaylistsRowDelegate {
    
    func didTapCompactPlaylist(_ playlist: CompactPlaylist) {
        let vc = PlaylistSongsVC.loadFromNib()
        vc.playlistId = playlist.playlistId
        self.navigationController?.pushViewController(vc, animated: true)
    }
}

extension HomeVC: SimplePlaylistsRowDelegate {
    
    func didTapSimplePlaylist(_ playlist: SimplePlaylist) {
        guard let playlistId = playlist.playlistId else { return }
        let vc = PlaylistSongsVC.loadFromNib()
        vc.playlistId = playlistId
        self.navigationController?.pushViewController(vc, animated: true)
    }
}

extension HomeVC: HomeArtistRowDelegate {
    
    func didTapArtist(_ artist: Artist) {
        let vc = ArtistSongsVC.loadFromNib()
        vc.artist = artist
        self.navigationController?.pushViewController(vc, animated: true)
    }
}

extension HomeVC: VideoListCellDelegate {
    
    func didTapTrack(_ track: DisplayedVideoItem, queue: [DisplayedVideoItem]) {
        playerController?.collapse()
        playerController?.setCurrentSong(track, queue: queue)
    }
}
