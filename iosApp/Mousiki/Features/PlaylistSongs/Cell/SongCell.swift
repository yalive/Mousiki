//
//  SongCell.swift
//  Mousiki
//
//  Created by A.Yabahddou on 2/21/21.
//  Copyright © 2021 YabaSoft. All rights reserved.
//

import UIKit
import shared

class SongCell: UITableViewCell {

    @IBOutlet weak var imgSong: UIImageView!
    @IBOutlet weak var txtSongTitle: UILabel!
    @IBOutlet weak var txtSongArtist: UILabel!
    @IBOutlet weak var txtDuration: UILabel!
    
    
    override func awakeFromNib() {
        super.awakeFromNib()
        imgSong.setCorner(radius: 8)
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)
    }
    
    
    func bind(_ songItem: DisplayedVideoItem) {
        txtSongTitle.text = songItem.songTitle
        txtSongArtist.text = songItem.artistName()
        txtDuration.text = songItem.songDuration
        
        if let url = URL(string: songItem.songImagePath) {
            imgSong.kf.setImage(with: url)
        }
        
    }
    
}
