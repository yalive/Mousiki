//
//  SimplePlaylistsRow.swift
//  Mousiki
//
//  Created by A.Yabahddou on 1/21/21.
//  Copyright © 2021 YabaSoft. All rights reserved.
//

import UIKit
import shared

protocol SimplePlaylistsRowDelegate: class {
    func didTapSimplePlaylist(_ playlist: SimplePlaylist)
}

class SimplePlaylistsRow: UITableViewCell, UICollectionViewDelegate, UICollectionViewDataSource, UICollectionViewDelegateFlowLayout {

    weak var delegate: SimplePlaylistsRowDelegate?
    
    @IBOutlet weak var collectionView: UICollectionView!
    
    private var playlists = [SimplePlaylist]()
    
    override func awakeFromNib() {
        super.awakeFromNib()
        
        // Preapare collection view
        self.collectionView.dataSource = self
        self.collectionView.delegate = self
        
        let flowLayout = UICollectionViewFlowLayout()
        flowLayout.itemSize = CGSize(width: 170, height: 220)
        flowLayout.sectionInset = UIEdgeInsets(top: 0, left: 12, bottom: 0, right: 8)
        flowLayout.scrollDirection = UICollectionView.ScrollDirection.horizontal
        flowLayout.minimumInteritemSpacing = 20.0
        self.collectionView.collectionViewLayout = flowLayout
        
        self.collectionView.register(UINib.init(nibName: "SimplePlaylistCell", bundle: nil), forCellWithReuseIdentifier: "SimplePlaylistCell")
        
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)
    }
    
    func bind(items: [SimplePlaylist]) {
        self.playlists = items
        self.collectionView.reloadData()
    }
    
    
    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        playlists.count
    }
    
    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        let cell = collectionView.dequeueReusableCell(withReuseIdentifier: "SimplePlaylistCell", for: indexPath as IndexPath) as! SimplePlaylistCell
        cell.bind(playlists[indexPath.row])
        return cell
    }
    
    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, minimumLineSpacingForSectionAt section: Int) -> CGFloat {
        return 16
    }
    
    func collectionView(_ collectionView: UICollectionView, didSelectItemAt indexPath: IndexPath) {
        delegate?.didTapSimplePlaylist(playlists[indexPath.row])
    }
    
}
