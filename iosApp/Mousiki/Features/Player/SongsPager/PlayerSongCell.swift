//
//  PlayerSongCell.swift
//  Mousiki
//
//  Created by A.Yabahddou on 5/3/21.
//  Copyright © 2021 YabaSoft. All rights reserved.
//

import UIKit
import shared
import Kingfisher

class PlayerSongCell: UICollectionViewCell {

    var song: DisplayedVideoItem! {
        didSet {
            if let url = URL(string: song.songImagePath) {
                imgSong.kf.setImage(with: url)
            }
        }
    }

    @IBOutlet weak var imgSong: UIImageView!

    override func awakeFromNib() {
        super.awakeFromNib()
        imgSong.setCorner(radius: 4)
        imgSong.setBorder(width: 0.1, color: .gray)
    }

}
