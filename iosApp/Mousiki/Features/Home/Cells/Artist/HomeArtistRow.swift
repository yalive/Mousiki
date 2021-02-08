//
//  HomeArtistRow.swift
//  Mousiki
//
//  Created by A.Yabahddou on 1/24/21.
//  Copyright © 2021 YabaSoft. All rights reserved.
//

import UIKit
import shared

class HomeArtistRow: UITableViewCell, UICollectionViewDelegate, UICollectionViewDataSource, UICollectionViewDelegateFlowLayout {

    @IBOutlet weak var collectionView: UICollectionView!
    
    private var artists = [Artist]()
    
    override func awakeFromNib() {
        super.awakeFromNib()
        
        // Preapare collection view
        self.collectionView.dataSource = self
        self.collectionView.delegate = self
        
        let flowLayout = UICollectionViewFlowLayout()
        flowLayout.scrollDirection = .vertical
        let width = (UIScreen.main.bounds.width - 12*2) / 3 - 8.0
        flowLayout.itemSize = CGSize(width: width, height: 180)
        flowLayout.sectionInset = UIEdgeInsets(top: 0, left: 12, bottom: 0, right: 12)
        
        self.collectionView.collectionViewLayout = flowLayout
        self.collectionView.register(UINib.init(nibName: "HomeArtistCell", bundle: nil), forCellWithReuseIdentifier: "HomeArtistCell")
        
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)
    }
    
    func bind(items: [Artist]) {
        self.artists = items
    }
    
    
    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        artists.count
    }
    
    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        let cell = collectionView.dequeueReusableCell(withReuseIdentifier: "HomeArtistCell", for: indexPath as IndexPath) as! HomeArtistCell
        cell.bind(artists[indexPath.row])
        return cell
    }
    
    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, minimumLineSpacingForSectionAt section: Int) -> CGFloat {
        return 1
    }
    
}

