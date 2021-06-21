//
//  UITableViewExt.swift
//  Mousiki
//
//  Created by A.Yabahddou on 1/20/21.
//  Copyright © 2021 YabaSoft. All rights reserved.
//

import UIKit

extension UITableView {
    
    func registerCellNib(_ nibName: String, identifier: String = "cell") {
        self.register(UINib(nibName: nibName, bundle: nil), forCellReuseIdentifier: identifier)
    }
}
