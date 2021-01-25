//
//  SimplePlaylistCell.swift
//  Mousiki
//
//  Created by A.Yabahddou on 1/21/21.
//  Copyright © 2021 YabaSoft. All rights reserved.
//

import UIKit
import shared

class SimplePlaylistCell: UICollectionViewCell {

    @IBOutlet weak var mainView: UIView!
    @IBOutlet weak var image: UIImageView!
    @IBOutlet weak var txtTitle: UILabel!
    
    override func awakeFromNib() {
        super.awakeFromNib()
    }
    
    func bind(_ playlist: SimplePlaylist) {
        txtTitle.text = playlist.title
        image.setCorner(radius: 3)
        
        if let url = URL(string: playlist.featuredImage ?? "") {
            image.kf.setImage(with: url)
        }
    }

}
