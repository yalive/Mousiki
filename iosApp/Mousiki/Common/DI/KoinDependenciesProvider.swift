//
//  KoinDependenciesProvider.swift
//  Mousiki
//
//  Created by A.Yabahddou on 2/3/21.
//  Copyright © 2021 YabaSoft. All rights reserved.
//

import Foundation
import shared

class KoinDependenciesProvider: IOSDependenciesProvider {
        
    lazy var storage: StorageApi = MousikiAppStorage()
}
