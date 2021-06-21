//
//  Config.swift
//  Mousiki
//
//  Created by A.Yabahddou on 2/3/21.
//  Copyright © 2021 YabaSoft. All rights reserved.
//

import Foundation
import shared
import FirebaseRemoteConfig

class FirebaseConfigDelegate: RemoteConfigDelegate {
    
    lazy var firebaseConfig: RemoteConfig = {
        let config = RemoteConfig.remoteConfig()
        let settings = RemoteConfigSettings()
        settings.minimumFetchInterval = 1800
        config.configSettings = settings
        let defaultConfig = RemoteAppConfig.Companion().defaultConfig()
        config.setDefaults(defaultConfig as [String: NSObject])
        return config
    }()
    
    func fetchAndActivate(completionHandler: @escaping (KotlinBoolean) -> Void) {
        firebaseConfig.fetchAndActivate { (status, error) in
            print("Got activation response")
            // TODO: mapping between KotlibBoolean and swift Bool
            let success = status == RemoteConfigFetchAndActivateStatus.successFetchedFromRemote
            if success {
                print("Activation response success")
                completionHandler(true)
            } else {
                print("Activation response error")
                completionHandler(false)
            }
        }
        
    }
    
    func getBoolean(key: String) -> Bool {
        return firebaseConfig[key].boolValue
    }
    
    func getInt(key: String) -> Int32 {
        return Int32(truncating: firebaseConfig[key].numberValue)
    }
    
    func getString(key: String) -> String {
        return firebaseConfig[key].stringValue ?? ""
    }
    
}
