//
//  Analytics.swift
//  Mousiki
//
//  Created by A.Yabahddou on 2/7/21.
//  Copyright © 2021 YabaSoft. All rights reserved.
//

import Foundation
import shared
import FirebaseAnalytics

class MousikiAnalytics: AnalyticsApi {
    
    
    func logEvent(name: String, params: [String : Any]) {
        print("++++++++ 👇👇👇👇👇👇👇👇👇👇👇👇👇👇👇  +++++++++")
        print("Log event \(name) ==> params: \(params)")
        Analytics.logEvent(name, parameters: params)
    }
    
    func logScreenView(screenName: String) {
        // TODO: 
        //Analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, parameters: <#T##[String : Any]?#>)
    }
}
