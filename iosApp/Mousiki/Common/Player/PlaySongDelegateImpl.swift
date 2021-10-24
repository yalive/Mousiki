//
//  PlaySongDelegateImpl.swift
//  Mousiki
//
//  Created by A.Yabahddou on 2/8/21.
//  Copyright © 2021 YabaSoft. All rights reserved.
//

import Foundation
import shared

class PlaySongDelegateImpl: PlaySongDelegate {
    
    
    var currentSong: Track?
    
    
    func playTrackFromQueue(
        track: Track,
        queue: [Track],
        completionHandler: @escaping (KotlinUnit?, Error?) -> Void) {
        
        completionHandler(KotlinUnit(), nil)
    }
    
    func isPlayingASong() -> Bool {
        // TODO: check if a song is playing
        return true
    }
}
