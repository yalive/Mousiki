//
//  LoadListAdDelegate.swift
//  Mousiki
//
//  Created by A.Yabahddou on 2/21/21.
//  Copyright © 2021 YabaSoft. All rights reserved.
//

import Foundation
import shared

class LoadListAdDelegate: GetListAdsDelegate {
    
    func insertAdsIn(
        items: [DisplayableItem],
        completionHandler: @escaping ([DisplayableItem]?, Error?) -> Void) {
        completionHandler(items, nil)
    }
    
    func populateAdsIn(
        resource: Kotlinx_coroutines_coreMutableStateFlow,
        completionHandler: @escaping (KotlinUnit?, Error?) -> Void) {
        
        completionHandler(KotlinUnit.init(), nil)
    }
}
