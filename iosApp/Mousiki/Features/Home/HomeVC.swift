//
//  HomeVC.swift
//  Mousiki
//
//  Created by A.Yabahddou on 1/20/21.
//  Copyright © 2021 YabaSoft. All rights reserved.
//

import UIKit
import shared

class HomeVC: UIViewController {

    override func viewDidLoad() {
        super.viewDidLoad()
        
        self.navigationController?.isNavigationBarHidden = true
       
        
        DemosKt.makeWork {
            print("Finished")
        }
    }

}
