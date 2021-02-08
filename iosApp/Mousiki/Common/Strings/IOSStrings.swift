//
//  IOSStrings.swift
//  Mousiki
//
//  Created by A.Yabahddou on 2/8/21.
//  Copyright © 2021 YabaSoft. All rights reserved.
//

import Foundation
import shared

class IOSStrings: Strings {
    
    /// Home
    lazy var artists: String = R.string.localizable.artists()
    lazy var genres: String = R.string.localizable.genres()
    lazy var titleNewRelease: String = R.string.localizable.newReleases()
    lazy var titleTopCharts: String = R.string.localizable.topCharts()
    
    /// Library
    lazy var emptyFavouriteList: String = "No fav"

}
