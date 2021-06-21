//
//  UIViewController+Helper.swift
//  Mousiki
//
//  Created by A.Yabahddou on 1/20/21.
//  Copyright © 2021 YabaSoft. All rights reserved.
//
import UIKit

extension UIViewController {
    
    var scendDelegate: SceneDelegate? {
        return self.view.window?.windowScene?.delegate as? SceneDelegate
    }
    
    static func loadFromNib() -> Self {
        func instantiateFromNib<T: UIViewController>() -> T {
            return T.init(nibName: String(describing: T.self), bundle: nil)
        }
        
        return instantiateFromNib()
    }
    
    func popBack() {
        self.navigationController?.popViewController(animated: true)
    }
    
    func push(_ vc: UIViewController, animated: Bool = true) {
        self.navigationController?.pushViewController(vc, animated: animated)
    }
}

