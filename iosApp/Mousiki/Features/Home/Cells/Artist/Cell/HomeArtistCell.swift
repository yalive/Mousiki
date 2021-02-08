//
//  HomeArtistCell.swift
//  Mousiki
//
//  Created by A.Yabahddou on 1/24/21.
//  Copyright © 2021 YabaSoft. All rights reserved.
//

import UIKit
import shared

class HomeArtistCell: UICollectionViewCell {
    @IBOutlet weak var imageView: UIImageView!
    @IBOutlet weak var txtArtistName: UILabel!
    
    @IBOutlet weak var imageHeight: NSLayoutConstraint!
    
    
    override func awakeFromNib() {
        super.awakeFromNib()
        
        let tapGesture = UITapGestureRecognizer(target: self, action: #selector(self.tapGesture(_:)))
        imageView.addGestureRecognizer(tapGesture)
        imageView.isUserInteractionEnabled = true
        
        let itemWidth = (UIScreen.main.bounds.width - 12*2) / 3 - 20.0
        let imageSize = min(120, itemWidth)
        imageHeight.constant = imageSize
        imageView.setCorner(radius: imageSize / 2)
    }
    
    func bind(_ artist: Artist) {
        txtArtistName.text = artist.name
        if let url = URL(string: artist.imageFullPath) {
            imageView.kf.setImage(with: url)
        }
    }
    
    @objc func tapGesture(_ sender: UITapGestureRecognizer) {
        sender.view?.showAnimation {
            
        }
        
    }
}
