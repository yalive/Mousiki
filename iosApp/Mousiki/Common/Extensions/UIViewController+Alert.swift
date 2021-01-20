//
//  UIViewController+Alert.swift
//  Mousiki
//
//  Created by A.Yabahddou on 1/20/21.
//  Copyright © 2021 YabaSoft. All rights reserved.
//


import UIKit
import EZAlertController

// MARK: - UIApplication Extension
extension UIApplication {
    static var visibleViewController: UIViewController? {
        var currentVc = UIApplication.shared.keyWindow?.rootViewController
        while let presentedVc = currentVc?.presentedViewController {
            if let navVc = (presentedVc as? UINavigationController)?.viewControllers.last {
                currentVc = navVc
            } else if let tabVc = (presentedVc as? UITabBarController)?.selectedViewController {
                currentVc = tabVc
            } else {
                currentVc = presentedVc
            }
        }
        return currentVc
    }
}


extension UIViewController {
    
    func showAlert(title: String = "oops", message: String, acceptMessage: String = "ok", completion: (() -> Void)? = nil) {
        if UIApplication.visibleViewController?.isKind(of: UIAlertController.self) == false {
            EZAlertController.alert(title.localized, message: message.localized, acceptMessage: acceptMessage) { () -> Void in
                completion?()
            }
        }
    }
    
    func confirmAction(message: String, title: String = "Confirmer", acceptTitle: String = "Oui", cancelTitle: String = "Annuler", completion: ((Bool) -> Void)? = nil){
        if UIApplication.visibleViewController?.isKind(of: UIAlertController.self) == false {
            
            let cancelAction = UIAlertAction(title: cancelTitle.localized, style: .cancel) { action in
                completion?(false)
            }
            
            let yesAction = UIAlertAction(title: acceptTitle.localized, style: .default) { action in
                completion?(true)
            }
            
            EZAlertController.alert(title.localized, message: message.localized, actions: [cancelAction, yesAction])
        }
    }
}
