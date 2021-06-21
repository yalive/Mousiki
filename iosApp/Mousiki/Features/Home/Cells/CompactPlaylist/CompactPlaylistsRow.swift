//
//  CompactPlaylistsRow.swift
//  Mousiki
//
//  Created by A.Yabahddou on 1/21/21.
//  Copyright © 2021 YabaSoft. All rights reserved.
//

import UIKit
import shared

protocol CompactPlaylistsRowDelegate: class {
    func didTapCompactPlaylist(_ playlist: CompactPlaylist)
}

class CompactPlaylistsRow: UITableViewCell, UICollectionViewDelegate, UICollectionViewDataSource, UICollectionViewDelegateFlowLayout {

    weak var delegate: CompactPlaylistsRowDelegate?
    
    @IBOutlet weak var collectionView: UICollectionView!
    
    private var playlists = [CompactPlaylist]()
    
    override func awakeFromNib() {
        super.awakeFromNib()
        
        // Preapare collection view
        self.collectionView.dataSource = self
        self.collectionView.delegate = self
        
        let flowLayout = UICollectionViewFlowLayout()
        flowLayout.scrollDirection = .horizontal
        flowLayout.itemSize = CGSize(width: 200, height: 240)
        flowLayout.sectionInset = UIEdgeInsets(top: 0, left: 12, bottom: 0, right: 8)
        flowLayout.minimumInteritemSpacing = 20.0
        
        self.collectionView.collectionViewLayout = flowLayout
        self.collectionView.register(UINib.init(nibName: "CompactPlaylistCell", bundle: nil), forCellWithReuseIdentifier: "CompactPlaylistCell")
        
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)
    }
    
    func bind(items: [CompactPlaylist]) {
        self.playlists = items
        collectionView.reloadData()
    }
    
    
    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        playlists.count
    }
    
    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        let cell = collectionView.dequeueReusableCell(withReuseIdentifier: "CompactPlaylistCell", for: indexPath as IndexPath) as! CompactPlaylistCell
        cell.bind(playlists[indexPath.row])
        return cell
    }
    
    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, minimumLineSpacingForSectionAt section: Int) -> CGFloat {
        return 16
    }
    
    func collectionView(_ collectionView: UICollectionView, didSelectItemAt indexPath: IndexPath) {
        delegate?.didTapCompactPlaylist(playlists[indexPath.row])
    }
    
}
