//
//  ViewController.swift
//  Mousiki
//
//  Created by A.Yabahddou on 1/19/21.
//  Copyright © 2021 YabaSoft. All rights reserved.
//

import UIKit
import shared

class ViewController: UIViewController {

    override func viewDidLoad() {
        super.viewDidLoad()
        
        let track = MusicTrack(youtubeId: "5K3VXw9ywp8", title: "Relax", duration: "PT3M00S")
        let com = MusicTrack.Companion()
        let ytD = com.toYoutubeDuration(notificationDuration: track.duration)
        print(ytD)
        print("Track image url:\(track.durationToSeconds())")
    }


}

