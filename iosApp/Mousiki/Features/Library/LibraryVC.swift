//
//  LibraryVC.swift
//  Mousiki
//
//  Created by A.Yabahddou on 1/20/21.
//  Copyright © 2021 YabaSoft. All rights reserved.
//

import UIKit
import shared

class LibraryVC: UIViewController {
    
    fileprivate var items: [LibraryItem] = [
        LibraryItem.Playlists(items: []),
        LibraryItem.Recent(tracks: []),
        LibraryItem.Favourite(tracks: [])
    ]
    
    lazy var viewModel: LibraryViewModel = {
        return Injector().libraryViewModel
    }()
    @IBOutlet weak var tableView: UITableView!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        title = R.string.localizable.menu_library()
        observeViewModel()
    }
    
    override func viewWillAppear(_ animated: Bool) {
        viewModel.loadCustomPlaylists()
    }
    
    fileprivate func observeViewModel() {
        viewModel.playlistsFlow().watch { items in
            guard let items = items else { return }
            let playlists = items as! [LibraryPlaylistItem]
            print(playlists)
        }
    }
}


extension LibraryVC: UITableViewDataSource, UITableViewDelegate {
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        1
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        return UITableViewCell()
    }
    
    
    func numberOfSections(in tableView: UITableView) -> Int {
        return items.count
    }
    
}

