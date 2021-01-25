//
//  VideoListCell.swift
//  Mousiki
//
//  Created by A.Yabahddou on 1/21/21.
//  Copyright © 2021 YabaSoft. All rights reserved.
//

import UIKit
import shared

class VideoListCell: UITableViewCell, UICollectionViewDelegate, UICollectionViewDataSource, UICollectionViewDelegateFlowLayout {

    @IBOutlet weak var collectionView: UICollectionView!
    
    private var videos = [DisplayedVideoItem]()
    
    override func awakeFromNib() {
        super.awakeFromNib()
        
        // Initialization code
        self.collectionView.dataSource = self
        self.collectionView.delegate = self
        
        let flowLayout = UICollectionViewFlowLayout()
        flowLayout.scrollDirection = .horizontal
        flowLayout.itemSize = CGSize(width: 160, height: 200)
        flowLayout.sectionInset = UIEdgeInsets(top: 0, left: 12, bottom: 0, right: 8)
        flowLayout.minimumInteritemSpacing = 20.0
        self.collectionView.collectionViewLayout = flowLayout
        
        self.collectionView.register(UINib.init(nibName: "VideoCollectionCell", bundle: nil), forCellWithReuseIdentifier: "cell")
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)
    }
    
    
    func bind(items: [DisplayedVideoItem]) {
        self.videos = items
        
        self.collectionView.reloadData()
    }
    
    
    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        videos.count
    }
    
    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        let cell = collectionView.dequeueReusableCell(withReuseIdentifier: "cell", for: indexPath as IndexPath) as! VideoCollectionCell
        cell.bind(videos[indexPath.row])
        return cell
    }
    
    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, minimumLineSpacingForSectionAt section: Int) -> CGFloat {
        return 16
    }
     
}
