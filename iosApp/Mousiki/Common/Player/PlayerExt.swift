//
//  PlayerExt.swift
//  Mousiki
//
//  Created by A.Yabahddou on 5/2/21.
//  Copyright © 2021 YabaSoft. All rights reserved.
//

import UIKit

extension UIViewController {

    var playerController: PlayerVC? {
        (tabBarController as? TabBarController)?.playerVC
    }
}
