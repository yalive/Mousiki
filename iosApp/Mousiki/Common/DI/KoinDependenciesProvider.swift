//
//  KoinDependenciesProvider.swift
//  Mousiki
//
//  Created by A.Yabahddou on 2/3/21.
//  Copyright © 2021 YabaSoft. All rights reserved.
//

import Foundation
import shared


/// Provides iOS specific implementation for common dependencies
class KoinDependenciesProvider: IOSDependenciesProvider {
    
    
    /// Storage api
    lazy var storage: StorageApi = MousikiAppStorage()
    
    
    /// Remote config delegate
    lazy var remoteConfigDelegate: RemoteConfigDelegate = FirebaseConfigDelegate()
    
    
    /// Analytics api
    lazy var analytics: AnalyticsApi = MousikiAnalytics()
    
    // Player
    lazy var playSongDelegate: PlaySongDelegate = PlaySongDelegateImpl()
    
    
    lazy var strings: Strings = IOSStrings()
}
