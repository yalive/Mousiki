//
//  VideoCollectionCell.swift
//  Mousiki
//
//  Created by A.Yabahddou on 1/21/21.
//  Copyright © 2021 YabaSoft. All rights reserved.
//

import UIKit
import shared
import Kingfisher

class VideoCollectionCell: UICollectionViewCell {
    
    @IBOutlet weak var mainView: UIView!
    @IBOutlet weak var image: UIImageView!
    @IBOutlet weak var txtTitle: UILabel!
    @IBOutlet weak var txtDuration: UILabel!
    
    override func awakeFromNib() {
        super.awakeFromNib()
    }

    func bind(_ video: DisplayedVideoItem) {
        txtTitle.text = video.songTitle
        image.setCorner(radius: 3)
        if let url = URL(string: video.songImagePath) {
            image.kf.setImage(with: url)
        }
    }
}
