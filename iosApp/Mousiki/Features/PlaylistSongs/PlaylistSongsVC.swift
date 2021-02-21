//
//  PlaylistSongsVC.swift
//  Mousiki
//
//  Created by A.Yabahddou on 2/21/21.
//  Copyright © 2021 YabaSoft. All rights reserved.
//

import UIKit
import shared

class PlaylistSongsVC: UIViewController {
    
    var playlistId = ""
    private var songList = [DisplayableItem]()
    lazy var viewModel: PlaylistSongsViewModel = {
        return Injector().playlistSongsViewModel
    }()
    @IBOutlet weak var tableView: UITableView!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        tableView.registerCellNib("SongCell",identifier: "SongCell")
        self.navigationController?.isNavigationBarHidden = false
        observeViewModel()
        viewModel.doInit(playlistId: playlistId)
        print("Playlist: \(playlistId)")
    }
    
    fileprivate func observeViewModel() {
        viewModel.songsFlow().watch { resource in
            guard let resource = resource else {
                print("Goot nil data flow!!")
                return
            }
            
            if resource is ResourceLoading {
                print("Loading songs")
            } else if resource is ResourceSuccess {
                print("Got songs")
                self.songList = (resource as! ResourceSuccess).data as! [DisplayableItem]
                self.tableView.reloadData()
            } else if resource is ResourceFailure {
                print("Error load songs")
            }
        }
    }
}


extension PlaylistSongsVC: UITableViewDelegate, UITableViewDataSource {
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let songItem = songList[indexPath.row] as! DisplayedVideoItem
        let cell = tableView.dequeueReusableCell(withIdentifier: "SongCell") as! SongCell
        cell.bind(songItem)
        return cell
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return songList.count
    }
    
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return 80
    }
}
